package frontendB;

import shared.datamodel.Model;
import shared.serviceintf.Service;

public class Client {

	public void doB() {
		Service service = new Service();
		Model model = service.getModel();
		model.toString();
	}

}
