package model;

import java.lang.reflect.Constructor;

import application.Models;
import services.Service;

public class ModelFactory extends Service {
	public ModelFactory() {
		super("ModelFactory");
	}

	public static Model create(Class<?> clazz, Object... args){
		try{
			Class[] cArgs = new Class[args.length];
			for(int i =0; i < args.length; i++)
				cArgs[i] = args[i].getClass();
			
			Constructor<?> constr = clazz.getDeclaredConstructor(cArgs);
			constr.setAccessible(true);
			Object obj = constr.newInstance(args);
			if(!(obj instanceof Model)){
				logger.warning("Could not create model as %s is not a child of Model", clazz);
				return null;
			}
			Model model = (Model)obj;
			Models.add(model);
			Models.initialize(model);
			return model;
		}catch(Exception e){
			e.printStackTrace();
			logger.warning("Could not create model %s", clazz);
			return null;
		}
	}
}
