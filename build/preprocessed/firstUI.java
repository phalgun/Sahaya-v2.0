
//import examples.cityguide.Util;
import java.io.IOException;
import java.util.Enumeration;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.location.*;
import java.io.InputStream;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;

import org.netbeans.microedition.util.SimpleCancellableTask;

/**
 * @author Sahaya v2.0
 */
public class firstUI extends MIDlet implements CommandListener {

    private boolean midletPaused = false;
    //<editor-fold defaultstate="collapsed" desc=" Generated Fields ">//GEN-BEGIN:|fields|0|
    private Form form;
    private StringItem stringItem;
    private StringItem stringItem1;
    private Form form2;
    private ChoiceGroup choiceGroup;
    private Alert alert1;
    private Form form1;
    private Command exitCommand;
    private Command exit;
    private Command Refresh;
    private Command Add;
    private Command List_landmarks;
    private Command save;
    private Command back;
    private Command delete;
    private Command okCommand5;
    private Command details;
    private Command delete_lms;
    private Command backCommand1;
    private Command Navigate;
    private Command cancelCommand;
    private Image image1;
    private SimpleCancellableTask task;
    private SimpleCancellableTask task1;
    //</editor-fold>//GEN-END:|fields|0|
    private LocationProvider lp;
    private Coordinates c;
    private Location l;
    private Criteria cr;
    private String lat;
    private String lon;
    private LandmarkStore landmarkStore;
    private Landmark land;
    private boolean landmarkstore_exists;
    private String Destination_String;
    private QualifiedCoordinates source_coor;
    private Landmark Destination;
    // private Landmark Nearest_Landmark;
    private Landmark min_distance_land;
    private QualifiedCoordinates Previous_sourc_coor;
    private Location location;
    private LocationProvider loc_provider;
    private Criteria criteria;
    private LocationListener loc_listener;
    private boolean reached;
    private double range;
    private Location Proximity_Location;
    private boolean killthread;
    //private float prev_angle;

    public firstUI() {
        try {
            criteria = new Criteria();
            criteria.setHorizontalAccuracy(50);
            criteria.setVerticalAccuracy(50);
            loc_provider = LocationProvider.getInstance(criteria);
        } catch (LocationException ex) {
            //    ex.printStackTrace();
        }

    }

    private void loadLandmarks() {
        final InputStream is = getClass().getResourceAsStream("/LANDMARKS.txt");

        try {
            if (landmarkstore_exists) {
                Util.readLandmarksFromStream(landmarkStore, is);
            }
            //landmarkstore_exists = true;
        } catch (Exception ioe) {
            System.out.println("Cannot read landmarks.\n Landmark store wasn't created."
                    + ioe.getMessage());
            try {
                LandmarkStore.deleteLandmarkStore("landmarks");
            } catch (Exception e) {
            }
        }
    }

    //<editor-fold defaultstate="collapsed" desc=" Generated Methods ">//GEN-BEGIN:|methods|0|
    //</editor-fold>//GEN-END:|methods|0|
    //<editor-fold defaultstate="collapsed" desc=" Generated Method: initialize ">//GEN-BEGIN:|0-initialize|0|0-preInitialize
    /**
     * Initilizes the application.
     * It is called only once when the MIDlet is started. The method is called before the <code>startMIDlet</code> method.
     */
    private void initialize() {//GEN-END:|0-initialize|0|0-preInitialize
//GEN-LINE:|0-initialize|1|0-postInitialize
        // write post-initialize user code here
    }//GEN-BEGIN:|0-initialize|2|
    //</editor-fold>//GEN-END:|0-initialize|2|

    public class MyProximityListener_Thread implements Runnable {

        public void run() {
            MyProximityListener(min_distance_land);
        }
    }
    MyProximityListener_Thread MyProximity = new MyProximityListener_Thread();
    Thread ProximityListenerThread = new Thread(MyProximity);

    public class Go_To_Thread implements Runnable {

        public void run() {
            GoTo(min_distance_land);

        }
    }
    Go_To_Thread Go_To = new Go_To_Thread();
    Thread GoToThread = new Thread(Go_To);

    public class Nearest_Landmark_Thread implements Runnable {

        public void run() {

            Find_Nearest_Landmark(location.getQualifiedCoordinates(), Destination.getQualifiedCoordinates());

        }
    }
    Nearest_Landmark_Thread Found = new Nearest_Landmark_Thread();
    Thread Found_Nearest_Landmark_Thread = new Thread(Found);

    private void MyProximityListener(Landmark nextDestination) {



        if (Very_Close(Destination.getQualifiedCoordinates(), location.getQualifiedCoordinates()) && !reached) {
            System.out.println("Reached!");
            reached = true;

            killthread = true;
            form1.deleteAll();
            form1.append("Destination Reached");
            loc_listener = null;
            loc_provider.setLocationListener(null, 0, 0, 0);
            Alert alert = new Alert("Final Destination reached", null, null, AlertType.INFO);
            alert.setString("Final Destination reached\n");
            alert.setTimeout(Alert.FOREVER);
            
              try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("final destination reached.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }

           switchDisplayable(alert, form);
        } else if (!reached) {


            while (!Very_Close(location.getQualifiedCoordinates(), nextDestination.getQualifiedCoordinates()) && !killthread) {
                ;
            }
            if (!killthread) {

                Alert alert = new Alert(null, ("Reached\nPlease wait for further instructions"), null, null);
                alert.setTimeout(1000);
                form1.deleteAll();
                //form1.append("Wait..Collecting GPS data\n");

                switchDisplayable(alert, form1);
            }

        }

    }

    private synchronized Landmark Find_Nearest_Landmark(QualifiedCoordinates source_coordinates, QualifiedCoordinates dest_coor) {

        Landmark temp_land = null;
        double min_distance = 99999.0;
        double temp_distance;
        Enumeration e;
        source_coor = source_coordinates;
        //float source_destination_distance = dest_coor.distance(source_coor);
        min_distance_land = null;
        boolean get_out = false;
        range = 0.0003;
        if (!reached) {
            try {
                do {

                    e = landmarkStore.getLandmarks(null,
                            (source_coor.getLatitude()) - range,
                            (source_coor.getLatitude()) + range,
                            (source_coor.getLongitude()) - range,
                            (source_coor.getLongitude()) + range);

                    while (e.hasMoreElements() && e != null) {
                        temp_land = (Landmark) e.nextElement();
                        if (!(Very_Close(temp_land.getQualifiedCoordinates(), source_coor))
                                && !(Very_Close(temp_land.getQualifiedCoordinates(), Previous_sourc_coor))) {
                            temp_distance = dest_coor.distance(temp_land.getQualifiedCoordinates());
                            if (min_distance > temp_distance ) {
                                min_distance = temp_distance;
                                min_distance_land = temp_land;
                                get_out = true;
                            }
                        }
                    }
                    if (min_distance_land == null) {
                        range = range + 0.00005;
                    }

                } while (true && !get_out && range < 0.0005);

                if (min_distance_land != null) {
                    Previous_sourc_coor = source_coor;
                    //LocationProvider.addProximityListener(proximity_listener, min_distance_land.getQualifiedCoordinates(), (float) 20.0);

                    if (ProximityListenerThread.isAlive()) {
                        killthread = true;
                    }
                    MyProximityListener_Thread MyProximity = new MyProximityListener_Thread();
                    Thread ProximityListenerThread = new Thread(MyProximity);
                    killthread = false;
                    ProximityListenerThread.start();


                    if (!GoToThread.isAlive()) {
                        Go_To_Thread Go_To = new Go_To_Thread();
                        Thread GoToThread = new Thread(Go_To);
                        GoToThread.start();
                    }

                } else {
                    System.out.println("Out of Range");
                    form1.deleteAll();
                    form1.append("Out of range! No known landmarks nearby!");
                }

            } catch (Exception ex) {
            }
        }
        return null;
    }

    private boolean Very_Close(QualifiedCoordinates source_coor, QualifiedCoordinates dest_coor) {
        if (source_coor == null || dest_coor == null) {
            System.out.println("null coordinates");
            return false;
        }

        if (source_coor.distance(dest_coor) <= 10.0) {
            return true;
        } else {
            return false;
        }
    }

    private synchronized void GoTo(Landmark Nearest_Landmark) {
        if (!reached) {
            try {
                form1.deleteAll();
                String direction_String = new String();

                form1.append("Go To " + Nearest_Landmark.getName());

                //add code to generate verbal and textual directions as to how to get to the landmark
                //and if possible sleep for some time
                source_coor = location.getQualifiedCoordinates();
                QualifiedCoordinates dest_coor = Nearest_Landmark.getQualifiedCoordinates();

                if (source_coor == null) {
                    System.out.println("source_coor is null");
                    Alert alert = new Alert(null, ("source_coor is null"), null, null);
                    alert.setTimeout(3000);

                    switchDisplayable(alert, form1);
                }
                try {

                    MyOrientation Calc_Orientation = new MyOrientation(source_coor.getLatitude(), source_coor.getLongitude(), dest_coor.getLatitude(), dest_coor.getLongitude());

                    System.out.println("Angle in degrees due north is " + Calc_Orientation.courseAngleFromN);

                    direction_String = "\nnews";


                      if (true) {

                        float angle_diff = (Calc_Orientation.courseAngleFromN);

                        form1.append("\n angle difference is : " + angle_diff);


                        if (angle_diff >= 0.0 && angle_diff <= 20.0) {
                            direction_String = "\nMove east";
                             try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("east.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }

                            } else if (angle_diff > 340.0 && angle_diff < 360.0) {
                            direction_String = "\nMove east";
                             try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/east.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }
                        } else if (angle_diff > 20.0 && angle_diff < 70.0) {
                            direction_String = "\nMove north east";
                      try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/north east.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }

                        } else if (angle_diff >= 70.0 && angle_diff <= 110.0) {
                            direction_String = "\nMove north";
                      try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/north.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }

                        } else if (angle_diff > 110.0 && angle_diff < 160.0) {

                            direction_String = "\nMove north west";

                         try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/north west.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }
                        } else if (angle_diff >= 250.0 && angle_diff <= 290.0) {
                            direction_String = "\nMove south";

                         try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/south.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }
                        } else if (angle_diff > 200.0 && angle_diff < 250.0) {
                            direction_String = "\nMove south west";

                         try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/south west.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }
                        } else if (angle_diff > 290.0 && angle_diff < 340.0) {
                            direction_String = "\nMove south east";

                         try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/south east.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }
                        } else if (angle_diff >= 160.0 && angle_diff <= 200.0) {
                            direction_String = "\nMove west";
                         try {

                        Player player = Manager.createPlayer(getClass().getResourceAsStream("/west.mp3"),"audio/mpeg");
                        player.start();

                        } catch(Exception e) {
                        e.printStackTrace();
                    }

                        }
                        // prev_angle = angle_diff;
                    }
                    form1.append("\n\n" + direction_String + "\n");
                    /*
                    else {
                    float angle_diff = (Calc_Orientation.courseAngleFromN - prev_angle + 90) ;
                    form1.append("\n angle difference is : " + angle_diff);
                    if (angle_diff >= 0.0 && angle_diff <= 20.0) {
                    direction_String = "\nTake a right and move straight ahead";
                    }if (angle_diff >= 340.0 && angle_diff < 360.0) {
                    direction_String = "\nTake a right and move straight ahead";
                    }else if (angle_diff > 20.0 && angle_diff < 70.0) {
                    direction_String = "\nMove diaganolly towards right";
                    } else if (angle_diff >= 70.0 && angle_diff <= 110.0) {
                    direction_String = "\nMove straight ahead";
                    } else if (angle_diff > 110.0 && angle_diff < 160.0) {
                    direction_String = "\nMove diagonally towards left";
                    } else if (angle_diff >= 250.0 && angle_diff <= 290.0) {
                    direction_String = "\nTurn around and then move straight ahead";
                    } else if (angle_diff > 200.0 && angle_diff < 250.0) {
                    direction_String = "\nTurn around and then move diagonally towards your right";
                    } else if (angle_diff > 290.0 && angle_diff < 340.0) {
                    direction_String = "\nTurn around and then move diagonally towards your left";
                    } else if (angle_diff >= 160.0 && angle_diff <= 200.0) {
                    direction_String = "\nTake a left and move straight ahead";
                    }

                    }
                     */
                } catch (Exception e) {
                    Alert alert = new Alert(null, ("first" + e), null, null);
                    alert.setTimeout(3000);

                    switchDisplayable(alert, form1);

                }
            } catch (Exception ex) {
                Alert alert = new Alert(null, ("second" + ex), null, null);
                alert.setTimeout(3000);

                switchDisplayable(alert, form1);

            }
        }

    }

    private void Navigate(Landmark Destination) {
        try {
            reached = false;
            //prev_angle = (float) -1.0;
            location = loc_provider.getLocation(20);
            loc_listener = new MyLocationListener();
            loc_provider.setLocationListener(loc_listener, 20, 10, 10);
            //setProximityListener(Destination.getQualifiedCoordinates(), range);
            //LocationProvider.addProximityListener(proximity_listener, Destination.getQualifiedCoordinates(), (float) 20.0);

        } catch (Exception e) {
            // location unavailable
            Alert alert = new Alert(null, ("Location Unavailable..Probably poor satellite signal \n" + e), null, null);
            alert.setTimeout(3000);

            switchDisplayable(alert, form2);
        }

    }

    class MyLocationListener implements LocationListener {

        public synchronized void locationUpdated(LocationProvider lp, Location present_location) {
            if (!reached) {
                location = present_location;
                source_coor = location.getQualifiedCoordinates();
                if (!(Found_Nearest_Landmark_Thread.isAlive()) && present_location.isValid()) {
                    System.out.println("foundnearestlandmark thread startd");
                    Nearest_Landmark_Thread Found = new Nearest_Landmark_Thread();
                    Thread Found_Nearest_Landmark_Thread = new Thread(Found);
                    Found_Nearest_Landmark_Thread.start();

                }
            }
        }

        public void providerStateChanged(LocationProvider lp, int i) {
            //Utility.getStateString gets the state string such as 1 stands for Available
            String s = "providerStateChanged = ";
            Alert alert = new Alert("providerStateChanged", s, null, null);
            alert.setTimeout(2000);
            switchDisplayable(alert, form1);
        }
    }

    /* class MyProximityListener implements ProximityListener {

    public void proximityEvent(Coordinates coordinates, Location location) {

    if (Very_Close(Destination.getQualifiedCoordinates(), (QualifiedCoordinates) coordinates)) {
    System.out.println("Reached!");
    form1.deleteAll();
    form1.append("Destination Reached");
    loc_listener = null;
    loc_provider.setLocationListener(null,0,0,0);
    Alert alert = new Alert("Final Destination reached", null, null, AlertType.INFO);
    alert.setTimeout(Alert.FOREVER);
    switchDisplayable(alert, form);
    reached = true;

    } else {

    //add code , min_distance_land reached next landmark should be computed
    firstUI.this.source_coor = location.getQualifiedCoordinates();
    try {
    setProximityListener(min_distance_land.getQualifiedCoordinates(), range);
    //LocationProvider.removeProximityListener(proximity_listener);
    /*if (!Found_Nearest_Landmark_Thread.isAlive()) {
    Nearest_Landmark_Thread Found = new Nearest_Landmark_Thread();
    Thread Found_Nearest_Landmark_Thread = new Thread(Found);
    Found_Nearest_Landmark_Thread.start();
    }
    } catch (LocationException ex) {
    form1.append(""+ex);
    }
    Alert alert = new Alert(null, ("Reached\nPlease wait for further instructions"), null, null);
    alert.setTimeout(2000);
    form1.deleteAll();
    form1.append("Wait..Collecting GPS data");
    switchDisplayable(alert, form1);


    }
    /* if (location.isValid()) {
    if (!Found_Nearest_Landmark_Thread.isAlive() && location.isValid()) {

    Nearest_Landmark_Thread Found = new Nearest_Landmark_Thread();
    Thread Found_Nearest_Landmark_Thread = new Thread(Found);
     */
    /*
    public void monitoringStateChanged(boolean flag) {
    String s = "monitoringStateChanged invoked, isMonitoringActive = " + flag;
    Alert alert = new Alert("monitoringStateChanged", s, null, null);
    alert.setTimeout(2000);
    switchDisplayable(alert, form1);
    }
    }
    private boolean searchCategory(String s) {

    String temp = null;
    Enumeration Landmark_Categories;

    Landmark_Categories = landmarkStore.getCategories();

    if (Landmark_Categories == null) {
    return false;
    } else {
    while (Landmark_Categories.hasMoreElements()) {

    temp = (String) Landmark_Categories.nextElement();
    if (temp.equals(s)) {
    return true;
    }
    }
    return false;
    }
    }
     */
    private QualifiedCoordinates getcoor() {
        cr = new Criteria();
        // cr.setHorizontalAccuracy(50);
        // cr.setVerticalAccuracy(50);
        //cr.setCostAllowed(false);
        cr.setPreferredResponseTime(30000);

        try {
            lp = LocationProvider.getInstance(cr);
            l = lp.getLocation(60);

            c = l.getQualifiedCoordinates();
            lat = "" + c.getLatitude();
            lon = "" + c.getLongitude();
            stringItem.setText(lat);
            stringItem1.setText(lon);

            return (QualifiedCoordinates) c;

        } catch (Exception e) {
            System.out.println("error in getcoor");
            return null;
        }
    }

    private void buildLandmarkDirectory() {
        try {
            //LandmarkStore.createLandmarkStore("landmarks");
            //landmarkStore = LandmarkStore.getInstance("landmarks");
            //if(landmarkStore==null)
            //{
            LandmarkStore.createLandmarkStore("landmarks");
            landmarkStore = LandmarkStore.getInstance("landmarks");
            //}
            landmarkstore_exists = true;
            loadLandmarks();


        } catch (Exception e) {
            System.out.println("landmarkstore creation error" + e);
        }
    }

    private Landmark searchLandmarkStore(String landmark_name) {
        try {

            Enumeration landmarkEnumeration;
            landmarkEnumeration = landmarkStore.getLandmarks();
            if (landmarkEnumeration != null) {
                while (landmarkEnumeration.hasMoreElements()) {

                    land = (Landmark) (landmarkEnumeration.nextElement());
                    if (landmark_name.equals(land.getName())) {
                        return land;
                    }

                }
            }
        } catch (Exception e) {
            System.out.println("error in searchLandmarkStore" + e);
        }
        return null;
    }

    private void clearchoiceGroup() {
        while (choiceGroup.size() != 0) {
            choiceGroup.delete(0);
        }
    }

    private void listlandmarks() {

        try {
            clearchoiceGroup();
            Enumeration landmarkEnumeration;
            if ((landmarkStore = LandmarkStore.getInstance("landmarks")) != null) {
                landmarkEnumeration = landmarkStore.getLandmarks("Building", null);
                if (landmarkEnumeration != null) {
                    while (landmarkEnumeration.hasMoreElements()) {

                        land = (Landmark) (landmarkEnumeration.nextElement());
                        choiceGroup.append(land.getName(), null);

                    }
                }
            }
        } catch (Exception e) {
            System.out.println("error in listlandmarks" + e);
        }
    }

    /*private boolean deleteLandmark() {

    int currentIndex = choiceGroup.getSelectedIndex();
    if (currentIndex > -1 && landmarkstore_exists) {

    Landmark selectedLandmark = searchLandmarkStore(choiceGroup.getString(currentIndex));
    try {

    landmarkStore.deleteLandmark(selectedLandmark);
    return true;
    } catch (IOException ex) {
    System.out.println(ex);
    } catch (LandmarkException ex) {
    System.out.println(ex);
    }
    return true;
    }
    return false;
    }
     */
    //<editor-fold defaultstate="collapsed" desc=" Generated Method: startMIDlet ">//GEN-BEGIN:|3-startMIDlet|0|3-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Started point.
     */
    public void startMIDlet() {//GEN-END:|3-startMIDlet|0|3-preAction
        if ((landmarkStore = LandmarkStore.getInstance("landmarks")) == null) {
            landmarkstore_exists = false;
            buildLandmarkDirectory();
        } else {
            landmarkstore_exists = true;
        }

        switchDisplayable(null, getForm());//GEN-LINE:|3-startMIDlet|1|3-postAction
        //display.setCurrent(form);

        //getcoor();
    }//GEN-BEGIN:|3-startMIDlet|2|
    //</editor-fold>//GEN-END:|3-startMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: resumeMIDlet ">//GEN-BEGIN:|4-resumeMIDlet|0|4-preAction
    /**
     * Performs an action assigned to the Mobile Device - MIDlet Resumed point.
     */
    public void resumeMIDlet() {//GEN-END:|4-resumeMIDlet|0|4-preAction
        // write pre-action user code here
//GEN-LINE:|4-resumeMIDlet|1|4-postAction
        // write post-action user code here
    }//GEN-BEGIN:|4-resumeMIDlet|2|
    //</editor-fold>//GEN-END:|4-resumeMIDlet|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: switchDisplayable ">//GEN-BEGIN:|5-switchDisplayable|0|5-preSwitch
    /**
     * Switches a current displayable in a display. The <code>display</code> instance is taken from <code>getDisplay</code> method. This method is used by all actions in the design for switching displayable.
     * @param alert the Alert which is temporarily set to the display; if <code>null</code>, then <code>nextDisplayable</code> is set immediately
     * @param nextDisplayable the Displayable to be set
     */
    public void switchDisplayable(Alert alert, Displayable nextDisplayable) {//GEN-END:|5-switchDisplayable|0|5-preSwitch
        // write pre-switch user code here
        Display display = getDisplay();//GEN-BEGIN:|5-switchDisplayable|1|5-postSwitch
        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }//GEN-END:|5-switchDisplayable|1|5-postSwitch
        // write post-switch user code here
    }//GEN-BEGIN:|5-switchDisplayable|2|
    //</editor-fold>//GEN-END:|5-switchDisplayable|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Method: commandAction for Displayables ">//GEN-BEGIN:|7-commandAction|0|7-preCommandAction
    /**
     * Called by a system to indicated that a command has been invoked on a particular displayable.
     * @param command the Command that was invoked
     * @param displayable the Displayable where the command was invoked
     */
    public void commandAction(Command command, Displayable displayable) {//GEN-END:|7-commandAction|0|7-preCommandAction
        // write pre-action user code here
        if (displayable == form) {//GEN-BEGIN:|7-commandAction|1|31-preAction
            if (command == List_landmarks) {//GEN-END:|7-commandAction|1|31-preAction
                // write pre-action user code here
                switchDisplayable(null, getForm2());//GEN-LINE:|7-commandAction|2|31-postAction
                listlandmarks();
            } else if (command == Refresh) {//GEN-LINE:|7-commandAction|3|23-preAction

                switchDisplayable(null, getForm());//GEN-LINE:|7-commandAction|4|23-postAction

                getcoor();
            } else if (command == exit) {//GEN-LINE:|7-commandAction|5|21-preAction
                try {
                    LandmarkStore.deleteLandmarkStore("landmarks");
                    landmarkstore_exists = false;
                    killthread = true;
                    System.out.println("landmarkstore deleted");
                } catch (IOException ex) {
                    //ex.printStackTrace();
                } catch (LandmarkException ex) {
                    //ex.printStackTrace();
                }
                exitMIDlet();

//GEN-LINE:|7-commandAction|6|21-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|7|105-preAction
        } else if (displayable == form1) {
            if (command == back) {//GEN-END:|7-commandAction|7|105-preAction
                killthread = true;
                loc_provider.setLocationListener(null, 0, 0, 0);
                switchDisplayable(null, getForm2());//GEN-LINE:|7-commandAction|8|105-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|9|102-preAction
        } else if (displayable == form2) {
            if (command == Navigate) {//GEN-END:|7-commandAction|9|102-preAction
                Destination_String = choiceGroup.getString(choiceGroup.getSelectedIndex());

                switchDisplayable(null, getForm1());//GEN-LINE:|7-commandAction|10|102-postAction
                try {
                    Previous_sourc_coor = null;
                    Destination = searchLandmarkStore(Destination_String);
                    Navigate(Destination);
                } catch (Exception ex) {
                    //ex.printStackTrace();
                }
            } else if (command == back) {//GEN-LINE:|7-commandAction|11|40-preAction

                switchDisplayable(null, getForm());//GEN-LINE:|7-commandAction|12|40-postAction
                // write post-action user code here
            } else if (command == details) {//GEN-LINE:|7-commandAction|13|62-preAction
                alert1 = null;
                if (choiceGroup.getSelectedIndex() > -1 || !(landmarkstore_exists))
                    switchDisplayable(getAlert1(), getForm2());//GEN-LINE:|7-commandAction|14|62-postAction
                // write post-action user code here
            }//GEN-BEGIN:|7-commandAction|15|7-postCommandAction
        }//GEN-END:|7-commandAction|15|7-postCommandAction
        // write post-action user code here
    }//GEN-BEGIN:|7-commandAction|16|
    //</editor-fold>//GEN-END:|7-commandAction|16|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: form ">//GEN-BEGIN:|18-getter|0|18-preInit
    /**
     * Returns an initiliazed instance of form component.
     * @return the initialized component instance
     */
    public Form getForm() {
        if (form == null) {//GEN-END:|18-getter|0|18-preInit

            form = new Form("Coordinates are..", new Item[] { getStringItem(), getStringItem1() });//GEN-BEGIN:|18-getter|1|18-postInit
            form.addCommand(getExit());
            form.addCommand(getRefresh());
            form.addCommand(getList_landmarks());
            form.setCommandListener(this);//GEN-END:|18-getter|1|18-postInit
            // write post-init user code here
        }//GEN-BEGIN:|18-getter|2|
        return form;
    }
    //</editor-fold>//GEN-END:|18-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem ">//GEN-BEGIN:|44-getter|0|44-preInit
    /**
     * Returns an initiliazed instance of stringItem component.
     * @return the initialized component instance
     */
    public StringItem getStringItem() {
        if (stringItem == null) {//GEN-END:|44-getter|0|44-preInit
            // write pre-init user code here
            stringItem = new StringItem("Latitude:", "", Item.PLAIN);//GEN-LINE:|44-getter|1|44-postInit
            // write post-init user code here
        }//GEN-BEGIN:|44-getter|2|
        return stringItem;
    }
    //</editor-fold>//GEN-END:|44-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: stringItem1 ">//GEN-BEGIN:|45-getter|0|45-preInit
    /**
     * Returns an initiliazed instance of stringItem1 component.
     * @return the initialized component instance
     */
    public StringItem getStringItem1() {
        if (stringItem1 == null) {//GEN-END:|45-getter|0|45-preInit
            // write pre-init user code here
            stringItem1 = new StringItem("Longitude:", "");//GEN-LINE:|45-getter|1|45-postInit
            // write post-init user code here
        }//GEN-BEGIN:|45-getter|2|
        return stringItem1;
    }
    //</editor-fold>//GEN-END:|45-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: form2 ">//GEN-BEGIN:|37-getter|0|37-preInit
    /**
     * Returns an initiliazed instance of form2 component.
     * @return the initialized component instance
     */
    public Form getForm2() {
        if (form2 == null) {//GEN-END:|37-getter|0|37-preInit
            // write pre-init user code here
            form2 = new Form("", new Item[] { getChoiceGroup() });//GEN-BEGIN:|37-getter|1|37-postInit
            form2.addCommand(getBack());
            form2.addCommand(getDetails());
            form2.addCommand(getNavigate());
            form2.setCommandListener(this);//GEN-END:|37-getter|1|37-postInit
            // write post-init user code here
        }//GEN-BEGIN:|37-getter|2|
        return form2;
    }
    //</editor-fold>//GEN-END:|37-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: choiceGroup ">//GEN-BEGIN:|39-getter|0|39-preInit
    /**
     * Returns an initiliazed instance of choiceGroup component.
     * @return the initialized component instance
     */
    public ChoiceGroup getChoiceGroup() {
        if (choiceGroup == null) {//GEN-END:|39-getter|0|39-preInit
            // write pre-init user code here
            choiceGroup = new ChoiceGroup("List of Landmarks", Choice.EXCLUSIVE);//GEN-BEGIN:|39-getter|1|39-postInit
            choiceGroup.setFitPolicy(Choice.TEXT_WRAP_DEFAULT);//GEN-END:|39-getter|1|39-postInit
            // write post-init user code here
        }//GEN-BEGIN:|39-getter|2|
        return choiceGroup;
    }
    //</editor-fold>//GEN-END:|39-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exitCommand ">//GEN-BEGIN:|16-getter|0|16-preInit
    /**
     * Returns an initiliazed instance of exitCommand component.
     * @return the initialized component instance
     */
    public Command getExitCommand() {
        if (exitCommand == null) {//GEN-END:|16-getter|0|16-preInit
            // write pre-init user code here
            exitCommand = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|16-getter|1|16-postInit
            // write post-init user code here
        }//GEN-BEGIN:|16-getter|2|
        return exitCommand;
    }
    //</editor-fold>//GEN-END:|16-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: exit ">//GEN-BEGIN:|20-getter|0|20-preInit
    /**
     * Returns an initiliazed instance of exit component.
     * @return the initialized component instance
     */
    public Command getExit() {
        if (exit == null) {//GEN-END:|20-getter|0|20-preInit
            // write pre-init user code here
            exit = new Command("Exit", Command.EXIT, 0);//GEN-LINE:|20-getter|1|20-postInit
            // write post-init user code here
        }//GEN-BEGIN:|20-getter|2|
        return exit;
    }
    //</editor-fold>//GEN-END:|20-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: Refresh ">//GEN-BEGIN:|22-getter|0|22-preInit
    /**
     * Returns an initiliazed instance of Refresh component.
     * @return the initialized component instance
     */
    public Command getRefresh() {
        if (Refresh == null) {//GEN-END:|22-getter|0|22-preInit
            // write pre-init user code here
            Refresh = new Command("Refresh", Command.OK, 0);//GEN-LINE:|22-getter|1|22-postInit
            // write post-init user code here
        }//GEN-BEGIN:|22-getter|2|
        return Refresh;
    }
    //</editor-fold>//GEN-END:|22-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: Add ">//GEN-BEGIN:|24-getter|0|24-preInit
    /**
     * Returns an initiliazed instance of Add component.
     * @return the initialized component instance
     */
    public Command getAdd() {
        if (Add == null) {//GEN-END:|24-getter|0|24-preInit
            // write pre-init user code here
            Add = new Command("add", Command.OK, 0);//GEN-LINE:|24-getter|1|24-postInit
            // write post-init user code here
        }//GEN-BEGIN:|24-getter|2|
        return Add;
    }
    //</editor-fold>//GEN-END:|24-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: List_landmarks ">//GEN-BEGIN:|30-getter|0|30-preInit
    /**
     * Returns an initiliazed instance of List_landmarks component.
     * @return the initialized component instance
     */
    public Command getList_landmarks() {
        if (List_landmarks == null) {//GEN-END:|30-getter|0|30-preInit
            // write pre-init user code here
            List_landmarks = new Command("List Landmarks", Command.OK, 0);//GEN-LINE:|30-getter|1|30-postInit
            // write post-init user code here
        }//GEN-BEGIN:|30-getter|2|
        return List_landmarks;
    }
    //</editor-fold>//GEN-END:|30-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: back ">//GEN-BEGIN:|32-getter|0|32-preInit
    /**
     * Returns an initiliazed instance of back component.
     * @return the initialized component instance
     */
    public Command getBack() {
        if (back == null) {//GEN-END:|32-getter|0|32-preInit
            // write pre-init user code here
            back = new Command("Back", Command.BACK, 0);//GEN-LINE:|32-getter|1|32-postInit
            // write post-init user code here
        }//GEN-BEGIN:|32-getter|2|
        return back;
    }
    //</editor-fold>//GEN-END:|32-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: save ">//GEN-BEGIN:|35-getter|0|35-preInit
    /**
     * Returns an initiliazed instance of save component.
     * @return the initialized component instance
     */
    public Command getSave() {
        if (save == null) {//GEN-END:|35-getter|0|35-preInit
            // write pre-init user code here
            save = new Command("save", Command.OK, 0);//GEN-LINE:|35-getter|1|35-postInit
            // write post-init user code here
        }//GEN-BEGIN:|35-getter|2|
        return save;
    }
    //</editor-fold>//GEN-END:|35-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: delete ">//GEN-BEGIN:|42-getter|0|42-preInit
    /**
     * Returns an initiliazed instance of delete component.
     * @return the initialized component instance
     */
    public Command getDelete() {
        if (delete == null) {//GEN-END:|42-getter|0|42-preInit
            // write pre-init user code here
            delete = new Command("Delete", Command.OK, 0);//GEN-LINE:|42-getter|1|42-postInit
            // write post-init user code here
        }//GEN-BEGIN:|42-getter|2|
        return delete;
    }
    //</editor-fold>//GEN-END:|42-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: image1 ">//GEN-BEGIN:|54-getter|0|54-preInit
    /**
     * Returns an initiliazed instance of image1 component.
     * @return the initialized component instance
     */
    public Image getImage1() {
        if (image1 == null) {//GEN-END:|54-getter|0|54-preInit
            // write pre-init user code here
            try {//GEN-BEGIN:|54-getter|1|54-@java.io.IOException
                image1 = Image.createImage("/images2.jpg");
            } catch (java.io.IOException e) {//GEN-END:|54-getter|1|54-@java.io.IOException
                //e.printStackTrace();
            }//GEN-LINE:|54-getter|2|54-postInit
            // write post-init user code here
        }//GEN-BEGIN:|54-getter|3|
        return image1;
    }
    //</editor-fold>//GEN-END:|54-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: okCommand5 ">//GEN-BEGIN:|56-getter|0|56-preInit
    /**
     * Returns an initiliazed instance of okCommand5 component.
     * @return the initialized component instance
     */
    public Command getOkCommand5() {
        if (okCommand5 == null) {//GEN-END:|56-getter|0|56-preInit
            // write pre-init user code here
            okCommand5 = new Command("Ok", Command.OK, 0);//GEN-LINE:|56-getter|1|56-postInit
            // write post-init user code here
        }//GEN-BEGIN:|56-getter|2|
        return okCommand5;
    }
    //</editor-fold>//GEN-END:|56-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: alert1 ">//GEN-BEGIN:|63-getter|0|63-preInit
    /**
     * Returns an initiliazed instance of alert1 component.
     * @return the initialized component instance
     */
    public Alert getAlert1() {
        if (alert1 == null) {//GEN-END:|63-getter|0|63-preInit
            int currentIndex = choiceGroup.getSelectedIndex();
            Landmark selectedLandmark = searchLandmarkStore(choiceGroup.getString(currentIndex));
            QualifiedCoordinates selectedLandmark_coordinates = selectedLandmark.getQualifiedCoordinates();
            String latlon = "\nLatitude is " + selectedLandmark_coordinates.getLatitude() + "\nLongitude is " + selectedLandmark_coordinates.getLongitude();

            alert1 = new Alert("Details ", null, null, AlertType.INFO);//GEN-BEGIN:|63-getter|1|63-postInit
            alert1.setTimeout(Alert.FOREVER);//GEN-END:|63-getter|1|63-postInit
            alert1.setString(latlon);
        }//GEN-BEGIN:|63-getter|2|
        return alert1;
    }
    //</editor-fold>//GEN-END:|63-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: details ">//GEN-BEGIN:|61-getter|0|61-preInit
    /**
     * Returns an initiliazed instance of details component.
     * @return the initialized component instance
     */
    public Command getDetails() {
        if (details == null) {//GEN-END:|61-getter|0|61-preInit
            // write pre-init user code here
            details = new Command("Details", Command.OK, 0);//GEN-LINE:|61-getter|1|61-postInit
            // write post-init user code here
        }//GEN-BEGIN:|61-getter|2|
        return details;
    }
    //</editor-fold>//GEN-END:|61-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: delete_lms ">//GEN-BEGIN:|70-getter|0|70-preInit
    /**
     * Returns an initiliazed instance of delete_lms component.
     * @return the initialized component instance
     */
    public Command getDelete_lms() {
        if (delete_lms == null) {//GEN-END:|70-getter|0|70-preInit
            // write pre-init user code here
            delete_lms = new Command("Delete LMS", Command.OK, 0);//GEN-LINE:|70-getter|1|70-postInit
            // write post-init user code here
        }//GEN-BEGIN:|70-getter|2|
        return delete_lms;
    }
    //</editor-fold>//GEN-END:|70-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task ">//GEN-BEGIN:|82-getter|0|82-preInit
    /**
     * Returns an initiliazed instance of task component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask() {
        if (task == null) {//GEN-END:|82-getter|0|82-preInit
            // write pre-init user code here
            task = new SimpleCancellableTask();//GEN-BEGIN:|82-getter|1|82-execute
            task.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|82-getter|1|82-execute
                    // write task-execution user code here
                }//GEN-BEGIN:|82-getter|2|82-postInit
            });//GEN-END:|82-getter|2|82-postInit
            // write post-init user code here
        }//GEN-BEGIN:|82-getter|3|
        return task;
    }
    //</editor-fold>//GEN-END:|82-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: task1 ">//GEN-BEGIN:|89-getter|0|89-preInit
    /**
     * Returns an initiliazed instance of task1 component.
     * @return the initialized component instance
     */
    public SimpleCancellableTask getTask1() {
        if (task1 == null) {//GEN-END:|89-getter|0|89-preInit
            // write pre-init user code here
            task1 = new SimpleCancellableTask();//GEN-BEGIN:|89-getter|1|89-execute
            task1.setExecutable(new org.netbeans.microedition.util.Executable() {
                public void execute() throws Exception {//GEN-END:|89-getter|1|89-execute
                }//GEN-BEGIN:|89-getter|2|89-postInit
            });//GEN-END:|89-getter|2|89-postInit
            // write post-init user code here
        }//GEN-BEGIN:|89-getter|3|
        return task1;
    }
    //</editor-fold>//GEN-END:|89-getter|3|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: backCommand1 ">//GEN-BEGIN:|94-getter|0|94-preInit
    /**
     * Returns an initiliazed instance of backCommand1 component.
     * @return the initialized component instance
     */
    public Command getBackCommand1() {
        if (backCommand1 == null) {//GEN-END:|94-getter|0|94-preInit
            // write pre-init user code here
            backCommand1 = new Command("Back", Command.BACK, 0);//GEN-LINE:|94-getter|1|94-postInit
            // write post-init user code here
        }//GEN-BEGIN:|94-getter|2|
        return backCommand1;
    }
    //</editor-fold>//GEN-END:|94-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: cancelCommand ">//GEN-BEGIN:|97-getter|0|97-preInit
    /**
     * Returns an initiliazed instance of cancelCommand component.
     * @return the initialized component instance
     */
    public Command getCancelCommand() {
        if (cancelCommand == null) {//GEN-END:|97-getter|0|97-preInit
            // write pre-init user code here
            cancelCommand = new Command("Cancel", Command.CANCEL, 0);//GEN-LINE:|97-getter|1|97-postInit
            // write post-init user code here
        }//GEN-BEGIN:|97-getter|2|
        return cancelCommand;
    }
    //</editor-fold>//GEN-END:|97-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: form1 ">//GEN-BEGIN:|103-getter|0|103-preInit
    /**
     * Returns an initiliazed instance of form1 component.
     * @return the initialized component instance
     */
    public Form getForm1() {
        if (form1 == null) {//GEN-END:|103-getter|0|103-preInit
            // write pre-init user code here
            form1 = new Form("Wait..Collecting GPS data");//GEN-BEGIN:|103-getter|1|103-postInit
            form1.addCommand(getBack());
            form1.setCommandListener(this);//GEN-END:|103-getter|1|103-postInit
            // write post-init user code here
        }//GEN-BEGIN:|103-getter|2|
        return form1;
    }
    //</editor-fold>//GEN-END:|103-getter|2|

    //<editor-fold defaultstate="collapsed" desc=" Generated Getter: Navigate ">//GEN-BEGIN:|101-getter|0|101-preInit
    /**
     * Returns an initiliazed instance of Navigate component.
     * @return the initialized component instance
     */
    public Command getNavigate() {
        if (Navigate == null) {//GEN-END:|101-getter|0|101-preInit
            // write pre-init user code here
            Navigate = new Command("Navigate", Command.OK, 0);//GEN-LINE:|101-getter|1|101-postInit
            // write post-init user code here
        }//GEN-BEGIN:|101-getter|2|
        return Navigate;
    }
    //</editor-fold>//GEN-END:|101-getter|2|

    public Display getDisplay() {
        return Display.getDisplay(this);
    }

    public void exitMIDlet() {
        /*try {
        LandmarkStore.deleteLandmarkStore("landmarks");
        } catch (Exception e) {}*/
        switchDisplayable(null, null);
        destroyApp(true);
        notifyDestroyed();
    }

    /**
     * Called when MIDlet is started.
     * Checks whether the MIDlet have been already started and initialize/starts or resumes the MIDlet.
     */
    public void startApp() {
        if (midletPaused) {
            resumeMIDlet();
        } else {
            initialize();
            startMIDlet();
        }
        midletPaused = false;
    }

    public void pauseApp() {
        midletPaused = true;
    }

    public void destroyApp(boolean unconditional) {
    }
}
