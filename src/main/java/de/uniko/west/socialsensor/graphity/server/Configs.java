package de.uniko.west.socialsensor.graphity.server;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

import de.uniko.west.socialsensor.graphity.socialgraph.NeoGraphConfiguration;

/**
 * This is an interface class to the Config file for this project. For each
 * class field on java property must be defined int config.txt. The fields will
 * be automatically filled! Allowed Types are String, int, String[] and long[]
 * where arrays are defined by semicolon-separated Strings like "array=a;b;c"
 * 
 * @author Jonas Kunze, Rene Pickhardt
 * 
 */
public class Configs extends Properties implements NeoGraphConfiguration {

	/**
	 * social graph database path
	 */
	private String databasePath;

	/**
	 * database read only flag<br>
	 * TODO: necessary or specified somewhere else?
	 */
	private boolean readOnly;

	/**
	 * cache type
	 */
	private String cacheType;

	/**
	 * use memory mapped buffers flag<br>
	 * TODO: boolean value?
	 */
	private String useMemoryMappedBuffers;

	/**
	 * social graph algorithm
	 */
	private String algorithm;

	private static final long serialVersionUID = -4439565094382127683L;

	static Configs instance = null;

	public Configs() {
		String file = "config.txt";
		try {
			BufferedInputStream stream = new BufferedInputStream(
					new FileInputStream(file));
			this.load(stream);
			stream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			this.initialize();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fills all fields with the data defined in the config file.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void initialize() throws IllegalArgumentException,
			IllegalAccessException {
		Field[] fields = this.getClass().getFields();
		for (Field f : fields) {
			if (this.getProperty(f.getName()) == null) {
				System.err.print("Property '" + f.getName()
						+ "' not defined in config file");
			}
			if (f.getType().equals(String.class)) {
				f.set(this, this.getProperty(f.getName()));
			} else if (f.getType().equals(long.class)) {
				f.setLong(this, Long.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(int.class)) {
				f.setInt(this, Integer.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(boolean.class)) {
				f.setBoolean(this,
						Boolean.valueOf(this.getProperty(f.getName())));
			} else if (f.getType().equals(String[].class)) {
				f.set(this, this.getProperty(f.getName()).split(";"));
			} else if (f.getType().equals(int[].class)) {
				String[] tmp = this.getProperty(f.getName()).split(";");
				int[] ints = new int[tmp.length];
				for (int i = 0; i < tmp.length; i++) {
					ints[i] = Integer.parseInt(tmp[i]);
				}
				f.set(this, ints);
			} else if (f.getType().equals(long[].class)) {
				String[] tmp = this.getProperty(f.getName()).split(";");
				long[] longs = new long[tmp.length];
				for (int i = 0; i < tmp.length; i++) {
					longs[i] = Long.parseLong(tmp[i]);
				}
				f.set(this, longs);
			}
		}
	}

	public static Configs get() {
		if (instance == null) {
			instance = new Configs();
		}
		return instance;
	}

	@Override
	public String databasePath() {
		return this.databasePath;
	}

	@Override
	public boolean readOnly() {
		return this.readOnly;
	}

	@Override
	public String cacheType() {
		return this.cacheType;
	}

	@Override
	public String useMemoryMappedBuffers() {
		return this.useMemoryMappedBuffers;
	}

	/**
	 * access social graph algorithm
	 * 
	 * @return social graph algorithm
	 */
	public String algorithm() {
		return this.algorithm;
	}
}
