package biosimclient;

class BioSimFakeLocation implements BioSimPlot {

	private final double elevationM;
	private final double latitude;
	private final double longitude;

	BioSimFakeLocation(double latitudeDeg, double longitudeDeg, double elevationM) {
		this.latitude = latitudeDeg;
		this.longitude = longitudeDeg;
		this.elevationM = elevationM;
	}



	@Override
	public double getElevationM() {return elevationM;}

	@Override
	public double getLatitudeDeg() {return latitude;}

	@Override
	public double getLongitudeDeg() {return longitude;}


	@Override
	public String toString() {return latitude + "_" + longitude + "_" + elevationM;}

}
