package shared.servicelocator;

import shared.datamodel.Model;
import shared.serviceintf.ServiceInterface;

public class ServiceImpl implements ServiceInterface {

	public Model getModel() {
		return new Model();
	}

}
