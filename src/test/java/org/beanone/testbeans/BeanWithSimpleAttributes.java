package org.beanone.testbeans;

public class BeanWithSimpleAttributes {
	private int integer = 1;
	private Integer intObject = 2;
	private String string = "A string";
	private Object doubleAsObject = new Double(3);

	public Object getDoubleAsObject() {
		return doubleAsObject;
	}

	public int getInteger() {
		return integer;
	}

	public Integer getIntObject() {
		return intObject;
	}

	public String getString() {
		return string;
	}

	public void setDoubleAsObject(Object doubleAsObject) {
		this.doubleAsObject = doubleAsObject;
	}

	public void setInteger(int integer) {
		this.integer = integer;
	}

	public void setIntObject(Integer intObject) {
		this.intObject = intObject;
	}

	public void setString(String string) {
		this.string = string;
	}
}
