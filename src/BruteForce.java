// tell the compiler where to find the methods you will use.
// required when you create an applet

import java.applet.Applet;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// required to paint on screen

//Start the applet and define a few necessary variables
public class BruteForce extends Applet {
    private int N = 100;
    private Body bodies[] = new Body[10000];
    private TextField t1;
    private boolean shouldrun = false;

    //the bodies are initialized in circular orbits around the central mass.
    //This is just some physics to do that
    private static double circlev(double rx, double ry) {
        double solarmass = 1.98892e30;
        double r2 = Math.sqrt(rx * rx + ry * ry);
        double numerator = (6.67e-11) * 1e6 * solarmass;
        return Math.sqrt(numerator / r2);
    }

    private static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }

    // The first time we call the applet, this function will start
    public void init() {
        startthebodies(N);
        t1 = new TextField("100", 5);
        Button b2 = new Button("Restart");
        Button b1 = new Button("Stop");
        Label l1 = new Label("Number of bodies:");
        ButtonListener myButtonListener = new ButtonListener();
        b1.addActionListener(myButtonListener);
        b2.addActionListener(myButtonListener);
        add(l1);
        add(t1);
        add(b2);
        add(b1);
    }

    // This method gets called when the applet is terminated. It stops the code
    public void stop() {
        shouldrun = false;
    }

    //Called by the applet initally. It can be executed again by calling repaint();
    public void paint(Graphics g) {
        g.translate(250, 250); //Originally the origin is in the top right. Put it in its normal place

//check if we stopped the applet, and if not, draw the particles where they are
        if (shouldrun) {
            for (int i = 0; i < N; i++) {
                g.setColor(bodies[i].color);
                g.fillOval((int) Math.round(bodies[i].rx * 250 / 1e18), (int) Math.round(bodies[i].ry * 250 / 1e18), 8, 8);
            }
            //go through the Brute Force algorithm (see the function below)
            addforces(N);
            //go through the same process again until applet is stopped
            repaint();
        }
    }

    //Initialize N bodies with random positions and circular velocities
    private void startthebodies(int N) {
        // double radius = 1e18;        // radius of universe
        double solarmass = 1.98892e30;
        for (int i = 0; i < N; i++) {
            double px = 1e18 * exp(-1.8) * (.5 - Math.random());
            double py = 1e18 * exp(-1.8) * (.5 - Math.random());
            double magv = circlev(px, py);

            double absangle = Math.atan(Math.abs(py / px));
            double thetav = Math.PI / 2 - absangle;
            // double phiv = Math.random() * Math.PI;
            double vx = -1 * Math.signum(py) * Math.cos(thetav) * magv;
            double vy = Math.signum(px) * Math.sin(thetav) * magv;
            //Orient a random 2D circular orbit

            if (Math.random() <= .5) {
                vx = -vx;
                vy = -vy;
            }

            double mass = Math.random() * solarmass * 10 + 1e20;
            //Color the masses in green gradients by mass
//            int red = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
//            int blue = (int) Math.floor(mass * 254 / (solarmass * 10 + 1e20));
//            int green = 255;
//            Color color = new Color(red, green, blue);
           // bodies[i] = new Body(px, py, vx, vy, mass, color);
            bodies[i] = new Body(px, py, vx, vy, mass, Color.black);
        }
        //Put the central mass in
        bodies[0] = new Body(0, 0, 0, 0, 1e6 * solarmass, Color.red);//put a heavy body in the center

    }

    //Use the method in Body to reset the forces, then add all the new forces
    private void addforces(int N) {
        for (int i = 0; i < N; i++) {
            bodies[i].resetForce();
            //Notice-2 loops-->N^2 complexity
            for (int j = 0; j < N; j++) {
                if (i != j) bodies[i].addForce(bodies[j]);
            }
        }
        //Then, loop again and update the bodies using timestep dt
        for (int i = 0; i < N; i++) {
            bodies[i].update(1e11);
        }
    }

    public boolean action(Event e, Object o) {
        N = Integer.parseInt(t1.getText());
        if (N > 1000) {
            t1.setText("1000");
            N = 1000;
        }

        startthebodies(N);
        repaint();

        return true;
    }

    public class ButtonListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            // Get label of the button clicked in event passed in
            String arg = evt.getActionCommand();
            if (arg.equals("Restart")) {
                shouldrun = true;
                N = Integer.parseInt(t1.getText());
                if (N > 1000) {
                    t1.setText("1000");
                    N = 1000;
                }

                startthebodies(N);
                repaint();
            } else if (arg.equals("Stop"))
                stop();
        }
    }

}