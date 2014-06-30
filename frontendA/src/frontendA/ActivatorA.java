package frontendA;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class ActivatorA implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		Client client = new Client();
		client.doA();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
