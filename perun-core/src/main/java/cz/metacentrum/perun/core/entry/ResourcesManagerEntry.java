package cz.metacentrum.perun.core.entry;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.metacentrum.perun.core.api.AuthzResolver;
import cz.metacentrum.perun.core.api.Facility;
import cz.metacentrum.perun.core.api.Group;
import cz.metacentrum.perun.core.api.Member;
import cz.metacentrum.perun.core.api.PerunSession;
import cz.metacentrum.perun.core.api.Resource;
import cz.metacentrum.perun.core.api.ResourceTag;
import cz.metacentrum.perun.core.api.ResourcesManager;
import cz.metacentrum.perun.core.api.RichResource;
import cz.metacentrum.perun.core.api.Role;
import cz.metacentrum.perun.core.api.Service;
import cz.metacentrum.perun.core.api.ServicesPackage;
import cz.metacentrum.perun.core.api.User;
import cz.metacentrum.perun.core.api.Vo;
import cz.metacentrum.perun.core.api.exceptions.ConsistencyErrorException;
import cz.metacentrum.perun.core.api.exceptions.FacilityNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.GroupAlreadyAssignedException;
import cz.metacentrum.perun.core.api.exceptions.GroupAlreadyRemovedFromResourceException;
import cz.metacentrum.perun.core.api.exceptions.GroupNotDefinedOnResourceException;
import cz.metacentrum.perun.core.api.exceptions.GroupNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.InternalErrorException;
import cz.metacentrum.perun.core.api.exceptions.MemberNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.PrivilegeException;
import cz.metacentrum.perun.core.api.exceptions.RelationExistsException;
import cz.metacentrum.perun.core.api.exceptions.ResourceAlreadyRemovedException;
import cz.metacentrum.perun.core.api.exceptions.ResourceNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.ResourceTagAlreadyAssignedException;
import cz.metacentrum.perun.core.api.exceptions.ResourceTagNotAssignedException;
import cz.metacentrum.perun.core.api.exceptions.ResourceTagNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.ServiceAlreadyAssignedException;
import cz.metacentrum.perun.core.api.exceptions.ServiceNotAssignedException;
import cz.metacentrum.perun.core.api.exceptions.ServiceNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.ServicesPackageNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.VoNotExistsException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeAssignmentException;
import cz.metacentrum.perun.core.api.exceptions.WrongAttributeValueException;
import cz.metacentrum.perun.core.api.exceptions.WrongReferenceAttributeValueException;
import cz.metacentrum.perun.core.bl.PerunBl;
import cz.metacentrum.perun.core.bl.ResourcesManagerBl;
import cz.metacentrum.perun.core.impl.Utils;

/**
 * 
 * @author Slavek Licehammer glory@ics.muni.cz
 */
public class ResourcesManagerEntry implements ResourcesManager {

  final static Logger log = LoggerFactory.getLogger(ResourcesManagerEntry.class);

  private ResourcesManagerBl resourcesManagerBl;
  private PerunBl perunBl;

  public ResourcesManagerEntry(PerunBl perunBl) {
    this.perunBl = perunBl;
    this.resourcesManagerBl = perunBl.getResourcesManagerBl();
  }

  public ResourcesManagerEntry() {
  }

  public Resource getResourceById(PerunSession sess, int id) throws InternalErrorException, PrivilegeException, ResourceNotExistsException {
    Utils.checkPerunSession(sess);

    Resource resource = getResourcesManagerBl().getResourceById(sess, id);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.RPC)) {
      throw new PrivilegeException(sess, "getResourceById");
    }

    return resource;
  }

    @Override
    public RichResource getRichResourceById(PerunSession sess, int id) throws InternalErrorException, PrivilegeException, ResourceNotExistsException {
        Utils.checkPerunSession(sess);

        RichResource rr = getResourcesManagerBl().getRichResourceById(sess, id);

        // Authorization
        if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, rr.getVo()) &&
            !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, rr.getVo()) &&
            !AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, rr.getFacility())) {
            throw new PrivilegeException(sess, "getRichResourceById");
        }

        return rr;

    }

    public Resource getResourceByName(PerunSession sess, Vo vo, Facility facility, String name) throws InternalErrorException, PrivilegeException,
    ResourceNotExistsException, VoNotExistsException, FacilityNotExistsException {
    Utils.checkPerunSession(sess);
    
    getPerunBl().getVosManagerBl().checkVoExists(sess, vo);
    getPerunBl().getFacilitiesManagerBl().checkFacilityExists(sess, facility);
    
    Resource resource = getResourcesManagerBl().getResourceByName(sess, vo, facility, name);
        
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.RPC)) {
      throw new PrivilegeException(sess, "getResourceByName");
    }

    return resource;
  }

  public Resource createResource(PerunSession sess, Resource resource, Vo vo, Facility facility) throws InternalErrorException, PrivilegeException, VoNotExistsException, FacilityNotExistsException {
    Utils.checkPerunSession(sess);
    getPerunBl().getVosManagerBl().checkVoExists(sess, vo);
    getPerunBl().getFacilitiesManagerBl().checkFacilityExists(sess, facility);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) &&
    	!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, facility)) {
      throw new PrivilegeException(sess, "createResource");
    }

    return getResourcesManagerBl().createResource(sess, resource, vo, facility);
  }

  public void deleteResource(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException, RelationExistsException, ResourceAlreadyRemovedException, GroupAlreadyRemovedFromResourceException, FacilityNotExistsException {
    Utils.checkPerunSession(sess);
    Facility facility = getPerunBl().getFacilitiesManagerBl().getFacilityById(sess, resource.getFacilityId());
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, facility)) {
      throw new PrivilegeException(sess, "deleteResource");
    }

    getResourcesManagerBl().checkResourceExists(sess, resource);

    getResourcesManagerBl().deleteResource(sess, resource);
  }

  public void deleteAllResources(PerunSession sess, Vo vo) throws InternalErrorException, VoNotExistsException, PrivilegeException, RelationExistsException, ResourceAlreadyRemovedException, GroupAlreadyRemovedFromResourceException {
    Utils.checkPerunSession(sess);
    
    //Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo)) {
      throw new PrivilegeException(sess, "deleteAllResources");
    }

    getPerunBl().getVosManagerBl().checkVoExists(sess, vo);

    getResourcesManagerBl().deleteAllResources(sess, vo);
  }

  public Facility getFacility(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.SERVICE)) {
      throw new PrivilegeException(sess, "getFacility");
    }

    return getResourcesManagerBl().getFacility(sess, resource);
  }

  public void setFacility(PerunSession sess, Resource resource, Facility facility) throws InternalErrorException, ResourceNotExistsException, PrivilegeException, FacilityNotExistsException {
    Utils.checkPerunSession(sess);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.PERUNADMIN)) {
      throw new PrivilegeException(sess, "setFacility");
    }

    getResourcesManagerBl().checkResourceExists(sess, resource);
    getPerunBl().getFacilitiesManagerBl().checkFacilityExists(sess, facility);

    getResourcesManagerBl().setFacility(sess, resource, facility);
  }

  public Vo getVo(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);
    Vo vo = getPerunBl().getResourcesManagerBl().getVo(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.SERVICE)) {
      throw new PrivilegeException(sess, "getVo");
    }

    return vo;
  }

  public List<Member> getAllowedMembers(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);

    getPerunBl().getResourcesManagerBl().checkResourceExists(sess, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource)) {
      throw new PrivilegeException(sess, "getAllowedMembers");
    }

    return getResourcesManagerBl().getAllowedMembers(sess, resource);
  }
  
  public List<User> getAllowedUsers(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);

    getPerunBl().getResourcesManagerBl().checkResourceExists(sess, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource)) {
      throw new PrivilegeException(sess, "getAllowedUsers");
    }

    return getResourcesManagerBl().getAllowedUsers(sess, resource);
  }

  public List<Service> getAssignedServices(PerunSession sess, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, resource)) {
      throw new PrivilegeException(sess, "getAssignedServices");
    }

    return getResourcesManagerBl().getAssignedServices(sess, resource);
  }


  public void assignGroupToResource(PerunSession sess, Group group, Resource resource) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, WrongAttributeValueException, WrongReferenceAttributeValueException, GroupAlreadyAssignedException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource)) {
      throw new PrivilegeException(sess, "assignGroupToResource");
    }
    
    getPerunBl().getGroupsManagerBl().checkGroupExists(sess, group);
    
    getResourcesManagerBl().assignGroupToResource(sess, group, resource);
  }
  
  public void assignGroupsToResource(PerunSession perunSession, List<Group> groups, Resource resource) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, WrongAttributeValueException, WrongReferenceAttributeValueException, GroupAlreadyAssignedException {
    Utils.checkPerunSession(perunSession);
    Utils.notNull(groups, "groups");
    getResourcesManagerBl().checkResourceExists(perunSession, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "assignGroupsToResource");
    }
    
    for(Group g: groups) {
        getPerunBl().getGroupsManagerBl().checkGroupExists(perunSession, g);
    }
    
    getResourcesManagerBl().assignGroupsToResource(perunSession, groups, resource);
  }

  public void assignGroupToResources(PerunSession perunSession, Group group, List<Resource> resources) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, WrongAttributeValueException, WrongReferenceAttributeValueException, GroupAlreadyAssignedException {
    Utils.checkPerunSession(perunSession);
    Utils.notNull(resources, "resources");
    getPerunBl().getGroupsManagerBl().checkGroupExists(perunSession, group);

    // Authorization
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, group)) {
      throw new PrivilegeException(perunSession, "assignGroupToResources");
    }
    
    for(Resource r: resources) {
        getResourcesManagerBl().checkResourceExists(perunSession, r);
    }
    
    getResourcesManagerBl().assignGroupToResources(perunSession, group, resources);
  }
  
  public void removeGroupFromResource(PerunSession sess, Group group, Resource resource) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, GroupNotDefinedOnResourceException, GroupAlreadyRemovedFromResourceException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource)) {
      throw new PrivilegeException(sess, "removeGroupFromResource");
    }
    
    getPerunBl().getGroupsManagerBl().checkGroupExists(sess, group);
    
    getResourcesManagerBl().removeGroupFromResource(sess, group, resource);
  }
  
  public void removeGroupsFromResource(PerunSession perunSession, List<Group> groups, Resource resource) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, GroupNotDefinedOnResourceException, GroupAlreadyRemovedFromResourceException {
    Utils.checkPerunSession(perunSession);
    Utils.notNull(groups, "groups");
    getResourcesManagerBl().checkResourceExists(perunSession, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "removeGroupsFromResource");
    }
    
    for(Group g: groups) {
        getPerunBl().getGroupsManagerBl().checkGroupExists(perunSession, g);
    }
    
    getResourcesManagerBl().removeGroupsFromResource(perunSession, groups, resource);  
  }
  
  public void removeGroupFromResources(PerunSession perunSession, Group group, List<Resource> resources) throws InternalErrorException, PrivilegeException, GroupNotExistsException, ResourceNotExistsException, GroupNotDefinedOnResourceException, GroupAlreadyRemovedFromResourceException {
    Utils.checkPerunSession(perunSession);
    Utils.notNull(resources, "resources");
    getPerunBl().getGroupsManagerBl().checkGroupExists(perunSession, group);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, group)) {
      throw new PrivilegeException(perunSession, "removeGroupFromResources");
    }
    
    for(Resource r: resources) {
        getResourcesManagerBl().checkResourceExists(perunSession, r);
    }
    
    getResourcesManagerBl().removeGroupFromResources(perunSession, group, resources);  
  }

  public List<Group> getAssignedGroups(PerunSession sess, Resource resource) throws InternalErrorException, PrivilegeException, ResourceNotExistsException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    Facility facility = getPerunBl().getResourcesManagerBl().getFacility(sess, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, resource) &&
        !AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, facility)) {
      throw new PrivilegeException(sess, "getAssignedGroups");
    }

    return getResourcesManagerBl().getAssignedGroups(sess, resource);
  }

  public List<Resource> getAssignedResources(PerunSession sess, Group group) throws InternalErrorException, GroupNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);
    getPerunBl().getGroupsManagerBl().checkGroupExists(sess, group);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, group) &&
                 !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, group) &&
  		 !AuthzResolver.isAuthorized(sess, Role.GROUPADMIN, group)) {
      throw new PrivilegeException(sess, "getAssignedResources");
    }

    return getResourcesManagerBl().getAssignedResources(sess, group);
  }
  
  public List<RichResource> getAssignedRichResources(PerunSession sess, Group group) throws InternalErrorException, GroupNotExistsException, PrivilegeException {
	  Utils.checkPerunSession(sess);
	  getPerunBl().getGroupsManagerBl().checkGroupExists(sess, group);

	  // Authorization
	  if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, group) &&
                  !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, group) &&
		  !AuthzResolver.isAuthorized(sess, Role.GROUPADMIN, group)) {
		  throw new PrivilegeException(sess, "getAssignedRichResources");
	  }

	  return getResourcesManagerBl().getAssignedRichResources(sess, group);
  }

  public void assignService(PerunSession sess, Resource resource, Service service) throws InternalErrorException, PrivilegeException, ResourceNotExistsException, ServiceNotExistsException, ServiceAlreadyAssignedException, WrongAttributeValueException, WrongReferenceAttributeValueException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, resource)) {
      throw new PrivilegeException(sess, "assignService");
    }
    
    getPerunBl().getServicesManagerBl().checkServiceExists(sess, service);

    getResourcesManagerBl().assignService(sess, resource, service);
  }

  public void assignServicesPackage(PerunSession sess, Resource resource, ServicesPackage servicesPackage) throws InternalErrorException, PrivilegeException, ResourceNotExistsException, ServicesPackageNotExistsException, WrongAttributeValueException, WrongReferenceAttributeValueException {
    Utils.checkPerunSession(sess);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.PERUNADMIN)) {
      throw new PrivilegeException(sess, "assignServicesPackage");
    }
    
    getResourcesManagerBl().checkResourceExists(sess, resource);
    getPerunBl().getServicesManagerBl().checkServicesPackageExists(sess, servicesPackage);

    getResourcesManagerBl().assignServicesPackage(sess, resource, servicesPackage);
  }

  public void removeService(PerunSession sess, Resource resource, Service service) throws InternalErrorException, PrivilegeException, ResourceNotExistsException, ServiceNotExistsException, ServiceNotAssignedException {
    Utils.checkPerunSession(sess);
    getResourcesManagerBl().checkResourceExists(sess, resource);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, resource)) {
      throw new PrivilegeException(sess, "removeServices");
    }
    
    getPerunBl().getServicesManagerBl().checkServiceExists(sess, service);

    getResourcesManagerBl().removeService(sess, resource, service);
  }

  public void removeServicesPackage(PerunSession sess, Resource resource, ServicesPackage servicesPackage) throws InternalErrorException, PrivilegeException, ResourceNotExistsException, ServicesPackageNotExistsException {
    Utils.checkPerunSession(sess);
  
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.PERUNADMIN)) {
      throw new PrivilegeException(sess, "removeServicesPackage");
    }

    getResourcesManagerBl().checkResourceExists(sess, resource);
    getPerunBl().getServicesManagerBl().checkServicesPackageExists(sess, servicesPackage);

    getResourcesManagerBl().removeServicesPackage(sess, resource, servicesPackage);
  }

  public List<Resource> getResources(PerunSession sess, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException {
    Utils.checkPerunSession(sess);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo)) {
      throw new PrivilegeException(sess, "getResources");
    }

    getPerunBl().getVosManagerBl().checkVoExists(sess, vo);

    return getResourcesManagerBl().getResources(sess, vo);
  }
  
  public List<RichResource> getRichResources(PerunSession sess, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException {
	  Utils.checkPerunSession(sess);

	  // Authorization
	  if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo)) {
		  throw new PrivilegeException(sess, "getRichResources");
	  }

	  getPerunBl().getVosManagerBl().checkVoExists(sess, vo);

	  return getResourcesManagerBl().getRichResources(sess, vo);
  }

  public int getResourcesCount(PerunSession sess, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException {
    Utils.checkPerunSession(sess);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, vo) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, vo)) {
      throw new PrivilegeException(sess, "getResourcesCount");
    }

    getPerunBl().getVosManagerBl().checkVoExists(sess, vo);

    return getResourcesManagerBl().getResourcesCount(sess, vo);
  }

  public List<Resource> getAllowedResources(PerunSession sess, Member member) throws InternalErrorException, MemberNotExistsException, PrivilegeException {
    Utils.checkPerunSession(sess);
  
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.PERUNADMIN) && !AuthzResolver.isAuthorized(sess, Role.VOADMIN, member) 
            && !AuthzResolver.isAuthorized(sess, Role.SELF, member) && !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, member)) {
      throw new PrivilegeException(sess, "getAllowedResources");
    }

    getPerunBl().getMembersManagerBl().checkMemberExists(sess, member);

    return getResourcesManagerBl().getAllowedResources(sess, member);
  }

  public List<Resource> getAssignedResources(PerunSession sess, Member member) throws InternalErrorException, PrivilegeException, MemberNotExistsException {
    Utils.checkPerunSession(sess);
  
    getPerunBl().getMembersManagerBl().checkMemberExists(sess, member);
    Vo vo = getPerunBl().getMembersManagerBl().getMemberVo(sess, member);

    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, member) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, member) &&
        !AuthzResolver.isAuthorized(sess, Role.GROUPADMIN, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.SELF, member)) {
      throw new PrivilegeException(sess, "getAssignedResources");
    }

    return getResourcesManagerBl().getAssignedResources(sess, member);
  }
  
  public List<RichResource> getAssignedRichResources(PerunSession sess, Member member) throws InternalErrorException, PrivilegeException, MemberNotExistsException {
	  Utils.checkPerunSession(sess);

	  getPerunBl().getMembersManagerBl().checkMemberExists(sess, member);
      Vo vo = getPerunBl().getMembersManagerBl().getMemberVo(sess, member);
	  
	  // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, member) &&
        !AuthzResolver.isAuthorized(sess, Role.VOOBSERVER, member) &&
        !AuthzResolver.isAuthorized(sess, Role.GROUPADMIN, vo) &&
        !AuthzResolver.isAuthorized(sess, Role.SELF, member)) {
      throw new PrivilegeException(sess, "getAssignedRichResources");
    }

	  return getResourcesManagerBl().getAssignedRichResources(sess, member);
  }
  
  public Resource updateResource(PerunSession sess, Resource resource) throws ResourceNotExistsException, InternalErrorException, PrivilegeException {
    Utils.notNull(sess, "sess");
    resourcesManagerBl.checkResourceExists(sess, resource);
    
    // Authorization
    if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, resource)) {
      throw new PrivilegeException(sess, "updateResource");
    }

    return resourcesManagerBl.updateResource(sess, resource);
  }
  
  public ResourceTag createResourceTag(PerunSession perunSession, ResourceTag resourceTag, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    getPerunBl().getVosManagerBl().checkVoExists(perunSession, vo);
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "createResourceTag");
    }
    
    return resourcesManagerBl.createResourceTag(perunSession, resourceTag, vo);
  }
  
  public ResourceTag updateResourceTag(PerunSession perunSession, ResourceTag resourceTag) throws InternalErrorException, PrivilegeException, ResourceTagNotExistsException, VoNotExistsException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    getResourcesManagerBl().checkResourceTagExists(perunSession, resourceTag);
    Vo vo = getPerunBl().getVosManagerBl().getVoById(perunSession, resourceTag.getVoId());
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "updateResourceTag");
    }
    
    return resourcesManagerBl.updateResourceTag(perunSession, resourceTag);
  }
  
  public void deleteResourceTag(PerunSession perunSession, ResourceTag resourceTag) throws InternalErrorException, PrivilegeException, ResourceTagNotExistsException, VoNotExistsException, ResourceTagAlreadyAssignedException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    Vo vo = getPerunBl().getVosManagerBl().getVoById(perunSession, resourceTag.getVoId());
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "deleteResourceTag");
    }
    resourcesManagerBl.deleteResourceTag(perunSession, resourceTag);
  }

  public void deleteAllResourcesTagsForVo(PerunSession perunSession, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException, ResourceTagAlreadyAssignedException {
    Utils.notNull(perunSession, "perunSession");
    getPerunBl().getVosManagerBl().checkVoExists(perunSession, vo);
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "deleteAllResourcesTagsForVo");
    }
    resourcesManagerBl.deleteAllResourcesTagsForVo(perunSession, vo);
  }
  
  public void assignResourceTagToResource(PerunSession perunSession, ResourceTag resourceTag, Resource resource) throws InternalErrorException, PrivilegeException, ResourceTagNotExistsException, ResourceNotExistsException, ResourceTagAlreadyAssignedException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    resourcesManagerBl.checkResourceExists(perunSession, resource);
    resourcesManagerBl.checkResourceTagExists(perunSession, resourceTag);
    if(resourceTag.getVoId() != resource.getVoId()) throw new ConsistencyErrorException("ResourceTag is from other Vo than Resource to which you want to assign it.");
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "assignResourceTagToResource");
    }
    resourcesManagerBl.assignResourceTagToResource(perunSession, resourceTag, resource);
  }
 
  public void removeResourceTagFromResource(PerunSession perunSession, ResourceTag resourceTag, Resource resource) throws InternalErrorException, PrivilegeException, ResourceTagNotExistsException, ResourceNotExistsException, ResourceTagNotAssignedException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    resourcesManagerBl.checkResourceExists(perunSession, resource);
    resourcesManagerBl.checkResourceTagExists(perunSession, resourceTag);
    if(resourceTag.getVoId() != resource.getVoId()) throw new ConsistencyErrorException("ResourceTag is from other Vo than Resource to which you want to remove from.");
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "removeResourceTagFromResource");
    }
    resourcesManagerBl.removeResourceTagFromResource(perunSession, resourceTag, resource);
  }
  
  public void removeAllResourcesTagFromResource(PerunSession perunSession, Resource resource) throws InternalErrorException, PrivilegeException, VoNotExistsException, ResourceNotExistsException {
    Utils.notNull(perunSession, "perunSession");
    resourcesManagerBl.checkResourceExists(perunSession, resource);

    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "removeAllResourcesTagFromResource");
    }
    resourcesManagerBl.removeAllResourcesTagFromResource(perunSession, resource);
  }  
 
  public List<Resource> getAllResourcesByResourceTag(PerunSession perunSession, ResourceTag resourceTag) throws InternalErrorException, PrivilegeException, VoNotExistsException, ResourceTagNotExistsException {
    Utils.notNull(perunSession, "perunSession");
    Utils.notNull(resourceTag, "resourceTag");
    resourcesManagerBl.checkResourceTagExists(perunSession, resourceTag);
    Vo vo = getPerunBl().getVosManagerBl().getVoById(perunSession, resourceTag.getVoId());
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "getAllResourcesByResourceTag");
      //TODO: what about GROUPADMIN?
    }
    return resourcesManagerBl.getAllResourcesByResourceTag(perunSession, resourceTag);
  }
 
  public List<ResourceTag> getAllResourcesTagsForVo(PerunSession perunSession, Vo vo) throws InternalErrorException, PrivilegeException, VoNotExistsException {
    Utils.notNull(perunSession, "perunSession");  
    getPerunBl().getVosManagerBl().checkVoExists(perunSession, vo);
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, vo)) {
      throw new PrivilegeException(perunSession, "getAllResourcesTagsForVo");
      //TODO: what about GROUPADMIN?
    }
    
    return resourcesManagerBl.getAllResourcesTagsForVo(perunSession, vo);
  }
  
  public List<ResourceTag> getAllResourcesTagsForResource(PerunSession perunSession, Resource resource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
    Utils.notNull(perunSession, "perunSession");  
    resourcesManagerBl.checkResourceExists(perunSession, resource);
    
    if (!AuthzResolver.isAuthorized(perunSession, Role.VOADMIN, resource)) {
      throw new PrivilegeException(perunSession, "getAllResourcesTagsForResource");
      //TODO: What about GROUPADMIN?
    }
    
    return resourcesManagerBl.getAllResourcesTagsForResource(perunSession, resource);
  }
  
  public void copyAttributes(PerunSession sess, Resource sourceResource, Resource destinationResource) throws InternalErrorException, PrivilegeException, ResourceNotExistsException, WrongReferenceAttributeValueException {
        Utils.checkPerunSession(sess);

        getResourcesManagerBl().checkResourceExists(sess, sourceResource);
        getResourcesManagerBl().checkResourceExists(sess, destinationResource);
        
        // Authorization - facility admin of the both resources required
        if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, sourceResource)) {
            throw new PrivilegeException(sess, "copyAttributes");
        }
        if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, destinationResource)) {
            throw new PrivilegeException(sess, "copyAttributes");
        }
        
        getResourcesManagerBl().copyAttributes(sess, sourceResource, destinationResource);
    }

  public void copyServices(PerunSession sess, Resource sourceResource, Resource destinationResource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException, WrongAttributeValueException, WrongReferenceAttributeValueException {
        Utils.checkPerunSession(sess);

        getResourcesManagerBl().checkResourceExists(sess, sourceResource);
        getResourcesManagerBl().checkResourceExists(sess, destinationResource);
        
        // Authorization - facility admin of the both resources required
        if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, sourceResource)) {
            throw new PrivilegeException(sess, "copyServices");
        }
        if (!AuthzResolver.isAuthorized(sess, Role.FACILITYADMIN, destinationResource)) {
            throw new PrivilegeException(sess, "copyServices");
        }
        
        getResourcesManagerBl().copyServices(sess, sourceResource, destinationResource);
  }
  
  public void copyGroups(PerunSession sess, Resource sourceResource, Resource destinationResource) throws InternalErrorException, ResourceNotExistsException, PrivilegeException {
        Utils.checkPerunSession(sess);

        getResourcesManagerBl().checkResourceExists(sess, sourceResource);
        getResourcesManagerBl().checkResourceExists(sess, destinationResource);
        
        // Authorization - vo admin of the both resources required
        if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, sourceResource)) {
            throw new PrivilegeException(sess, "copyGroups");
        }
        if (!AuthzResolver.isAuthorized(sess, Role.VOADMIN, destinationResource)) {
            throw new PrivilegeException(sess, "copyGroups");
        }
        
        getResourcesManagerBl().copyGroups(sess, sourceResource, destinationResource);
  }
  
  /**
   * Gets the resourcesManagerBl for this instance.
   *
   * @return The resourcesManagerBl.
   */
  public ResourcesManagerBl getResourcesManagerBl() {
    return this.resourcesManagerBl;
  }

  /**
   * Sets the perunBl for this instance.
   *
   * @param perunBl The perunBl.
   */
  public void setPerunBl(PerunBl perunBl)
  {
        this.perunBl = perunBl;
  }

  /**
   * Sets the resourcesManagerBl for this instance.
   *
   * @param resourcesManagerBl The resourcesManagerBl.
   */
  public void setResourcesManagerBl(ResourcesManagerBl resourcesManagerBl)
  {
        this.resourcesManagerBl = resourcesManagerBl;
  }

  public PerunBl getPerunBl() {
    return this.perunBl;
  }


}
