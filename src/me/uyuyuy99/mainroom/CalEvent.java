package me.uyuyuy99.mainroom;

public class CalEvent {
	
	public String title;
	public long time;
	
	public CalEvent(String title, long time) {
		this.title = title;
		this.time = time;
	}
	
	@Override
	public int hashCode() {
		return (title + time).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof CalEvent) {
			CalEvent other = (CalEvent) o;
			return (other.title.equals(title) && other.time == time);
		}
		return false;
	}
	
}
