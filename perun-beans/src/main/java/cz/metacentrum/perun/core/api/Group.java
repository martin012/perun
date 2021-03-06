package cz.metacentrum.perun.core.api;

import cz.metacentrum.perun.core.api.Auditable;
import cz.metacentrum.perun.core.api.exceptions.rt.InternalErrorRuntimeException;
import cz.metacentrum.perun.core.api.BeansUtils;

/**
 * Group entity.
 *
 * @author Slavek Licehammer glory@ics.muni.cz
 */

public class Group extends Auditable implements Comparable<Group> {
    private int voId;
    private Integer parentGroupId;
    private String name;
    private String description;

    
    /**
     * Constructor.
     */
    public Group() {
    }

    /**
     * Constructor.
     *
     * @param vo          VO of the group
     * @param name        name of the Group
     * @param description description the the group  
     */
    public Group(String name, String description)  {
        this.name = name;
        this.description = description;
    }
    
    public Group(String name, String description, String createdAt, String createdBy, String modifiedAt, String modifiedBy, Integer createdByUid, Integer modifiedByUid){
        super(createdAt, createdBy, modifiedAt, modifiedBy, createdByUid, modifiedByUid);
        this.name = name;
        this.description = description;
    }
    
    public Group(int id, String name, String description, String createdAt, String createdBy, String modifiedAt, String modifiedBy, Integer createdByUid, Integer modifiedByUid){
        super(id, createdAt, createdBy, modifiedAt, modifiedBy, createdByUid, modifiedByUid);
        this.name = name;
        this.description = description;
    }

    @Deprecated
    public Group(int id, String name, String description, String createdAt, String createdBy, String modifiedAt, String modifiedBy){
        super(id, createdAt, createdBy, modifiedAt, modifiedBy, null, null);
        this.name = name;
        this.description = description;
        
    }
    
    public Group(Integer parentGroupId, String name, String description) {
        this(name, description);
        this.parentGroupId = parentGroupId;
    }
    
    public Group(String name, String description, String createdAt, String createdBy, String modifiedAt, String modifiedBy, Integer parentGroupId, Integer createdByUid, Integer modifiedByUid){
        super(createdAt, createdBy, modifiedAt, modifiedBy, createdByUid, modifiedByUid);
        this.name = name;
        this.description = description;
        this.parentGroupId = parentGroupId;
    }
    
    public Group(int id, String name, String description, String createdAt, String createdBy, String modifiedAt, String modifiedBy, Integer parentGroupId, Integer createdByUid, Integer modifiedByUid){
        super(id, createdAt, createdBy, modifiedAt, modifiedBy, createdByUid, modifiedByUid);
        this.name = name;
        this.description = description;
        this.parentGroupId = parentGroupId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        
    }
    
    public Integer getParentGroupId() {
      return parentGroupId;
    }

    public void setParentGroupId(Integer parentGroupId) {
      this.parentGroupId = parentGroupId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVoId() {
      return voId;
    }

    public void setVoId(int voId) {
      this.voId = voId;
    }

    
    public String getShortName() {
        if(name == null) return null;
        return name.substring(name.lastIndexOf(':')+1);
    }

    public void setShortName(String shortName) {
        if(name == null) {
          name = shortName;
        } else {
          this.name = name.substring(0, name.lastIndexOf(':') + 1) + shortName;
        }
    }
    
    
    
    public int compareTo(Group group) {
        if (group == null) throw new InternalErrorRuntimeException(new NullPointerException("Group group"));
        if (this.getId() != group.getId()) {
            return this.name.toLowerCase().compareTo(group.getName().toLowerCase());
        }
        return 0;
    }

    @Override
    public String serializeToString() {
      return this.getClass().getSimpleName() +":[" +
      "id=<" + getId() + ">" +
      ", parentGroupId=<" + (getParentGroupId() == null ? "\\0" : getParentGroupId()) + ">" +
      ", name=<" + (getName() == null ? "\\0" : BeansUtils.createEscaping(getName())) + ">" +
      ", shortName=<" + (getShortName() == null ? "\\0" : BeansUtils.createEscaping(getShortName())) + ">" +
      ", description=<" + (getDescription() == null ? "\\0" : BeansUtils.createEscaping(getDescription())) + ">" +
      ", voId=<" + getVoId() + ">" +
      ']';
    }
    
    @Override
    public String toString() {
      StringBuilder ret = new StringBuilder();
      ret.append(getClass().getSimpleName());
      ret.append(":[");
      ret.append("id='");
      ret.append(this.getId());
      ret.append("', parentGroupId='");
      ret.append(parentGroupId);
      ret.append("', name='");
      ret.append(name);
      ret.append("', shortName='");
      ret.append(this.getShortName());
      ret.append("', description='");
      ret.append(description);
      ret.append("', voId='");
      ret.append(voId);
      ret.append("']");
      return ret.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Group other = (Group) obj;
        if (this.getId() != other.getId()) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }
}
