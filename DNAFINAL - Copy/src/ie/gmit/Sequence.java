package ie.gmit;

public class Sequence {
	
	
	
	public String sequence;
	int length;
	private  String name;
	
	
	
	public void setName(String name) {
		this.name = name;
	}


	public Sequence(String sequence, String name) {
		super();
		this.sequence = sequence;
		this.name = name;
	}
	
	
	 public Sequence(String string) {
		this.sequence=string;
	}


	public String getSequence() {
		return sequence;
	}


	public void setSequence(String sequence) {
		this.sequence = sequence;
	}


	public int getLength() {
		return sequence.length();
	}


	public void setLength(int length) {
		this.length = length;
	}


	public String getName() {
		return name;
	}


	public String[] spectrum(int length) {

	        String[] spectrumArray = new String[sequence.length() - length + 1];
	        for (int i = 0; i < spectrumArray.length; i++)
	        	spectrumArray[i] = sequence.substring(i, i + length);

	        return spectrumArray;

	    }
}
