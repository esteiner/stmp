package frontendA;

import shared.datamodel.Model;
import shared.serviceintf.Service;

public class Client {

	public void doA() {
		Service service = new Service();
		Model model = service.getModel();
		printClassCloader(service.getModel());
		printClassCloader(model);
		System.out.println(model.toString());
	}
	
	private void printClassCloader(Object object) {
		System.out.println("ClassLoader: " + object.getClass().getClassLoader());
	}
}
