public class LatLng{
    double x, y;

    LatLng(double lat, double lon){
        this.x = lat;
        this.y = lon;
    }

    double dist(LatLng ll){
        return Math.sqrt( Math.pow(this.x - ll.x, 2.0) + Math.pow(this.y - ll.y, 2.0) );
    }
}
