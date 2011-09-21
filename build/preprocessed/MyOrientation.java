class mMath
{
    // constants
    static final double sq2p1 = 2.414213562373095048802e0;
    static final double sq2m1  = .414213562373095048802e0;
    static final double p4  = .161536412982230228262e2;
    static final double p3  = .26842548195503973794141e3;
    static final double p2  = .11530293515404850115428136e4;
    static final double p1  = .178040631643319697105464587e4;
    static final double p0  = .89678597403663861959987488e3;
    static final double q4  = .5895697050844462222791e2;
    static final double q3  = .536265374031215315104235e3;
    static final double q2  = .16667838148816337184521798e4;
    static final double q1  = .207933497444540981287275926e4;
    static final double q0  = .89678597403663861962481162e3;
    static final double PIO2 = 1.5707963267948966135E0;
    static final double nan = (0.0/0.0);
    // reduce
    private static double mxatan(double arg)
    {
        double argsq, value;

        argsq = arg*arg;
        value = ((((p4*argsq + p3)*argsq + p2)*argsq + p1)*argsq + p0);
        value = value/(((((argsq + q4)*argsq + q3)*argsq + q2)*argsq + q1)*argsq + q0);
        return value*arg;
    }

    // reduce
    private static double msatan(double arg)
    {
        if(arg < sq2m1)
            return mxatan(arg);
        if(arg > sq2p1)
            return PIO2 - mxatan(1/arg);
            return PIO2/2 + mxatan((arg-1)/(arg+1));
    }

    // implementation of atan
    public static double atan(double arg)
    {
        if(arg > 0)
            return msatan(arg);
        return -msatan(-arg);
    }

    public mMath()
    {
    }
}



public class MyOrientation  {

    double latitude1;
    double longitude1;
    double latitude2;
    double longitude2;
    public double angleRad;
    public float angleDeg;
    public float courseAngleFromN;

    /**
     * The Orientation constructor.
     */
    public MyOrientation() {
    }
    public MyOrientation(double lat1,double lon1,double lat2,double lon2){
        latitude1 = lat1;
        longitude1 = lon1;
        latitude2 = lat2;
        longitude2 = lon2;

        computeorientation();
    }

    static public double angle(double dx, double dy) {

        if (dx > 0.0 && dy >= 0.0) {
            return mMath.atan(dy / dx);
        } else if (dx > 0.0 && dy < 0.0) {
            return mMath.atan(dy / dx) + 2.0 * Math.PI;
        } else if (dx < 0.0) {
            return mMath.atan(dy / dx) + Math.PI;
        } else if (dx == 0.0 && dy > 0.0) {
            return Math.PI / 2.0;
        } else if (dx == 0.0 && dy < 0.0) {
            return 3.0 * Math.PI / 2.0;
        }

        return Double.NaN;
    }

    private void computeorientation() {
        //double dx = Double.valueOf((textField2.getString()).trim()).doubleValue() - Double.valueOf((textField.getString()).trim()).doubleValue();     //pointB.x - pointA.x;
        //double dy = Double.valueOf((textField3.getString()).trim()).doubleValue() - Double.valueOf((textField1.getString()).trim()).doubleValue();   //pointB.y - pointA.y;

        double dx = latitude2 - latitude1;
        double dy = longitude2 - longitude1;

         angleRad = angle(dx, dy);
         angleDeg = (360 + (float) Math.toDegrees(angleRad)) % 360;
         courseAngleFromN = (float) (360 + 180 - (int) angleDeg) % 360;


    }

}
