package code;

public class Ship {
	int shipX;
	int shipY;
	int passengers;

	public Ship(int shipX, int shipY, int passengers) {
		this.shipX = shipX;
		this.shipY = shipY;
		this.passengers = passengers;

	}

	public void printShip() {
		System.out.println("Passengers:" + this.passengers + " " + "X,Y: " + this.shipX + " " + this.shipY);
	}
}