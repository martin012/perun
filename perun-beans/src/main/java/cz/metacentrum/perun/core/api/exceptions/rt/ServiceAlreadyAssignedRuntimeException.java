package cz.metacentrum.perun.core.api.exceptions.rt;

import cz.metacentrum.perun.core.api.Service;

public class ServiceAlreadyAssignedRuntimeException extends PerunRuntimeException {
    static final long serialVersionUID = 0;

    private Service service;

    public ServiceAlreadyAssignedRuntimeException() {
        super();
    }

    public ServiceAlreadyAssignedRuntimeException(Service service) {
        super(service.toString());
        this.service = service;
    }

    public ServiceAlreadyAssignedRuntimeException(Throwable cause) {
        super(cause);
    }

    public ServiceAlreadyAssignedRuntimeException(Throwable cause, Service service) {
        super(service.toString(), cause);

        this.service = service;
    }

    public Service getUserId() {
        return service;
    }
}
