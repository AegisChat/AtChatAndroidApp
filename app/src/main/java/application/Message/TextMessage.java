package application.Message;

public abstract class TextMessage extends Message{
	
	private String context;
	private long time;
	protected int type;

	public TextMessage(){
		context = "";
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public int getType(){
		return type;
	}
}
