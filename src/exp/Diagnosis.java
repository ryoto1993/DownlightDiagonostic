package exp;

import LightingSystem.Light;
import LightingSystem.SocketClient;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.ArrayList;

public class Diagnosis extends Thread {
    ArrayList<Light> dim_lights = new ArrayList<>();
    Light light = new Light();

    double lum_interval;
    double cct_interval;
    final double min_cct = 3000;
    final double max_cct = 6000;
    int time_interval = 3000;

    Label label_current;

    public Diagnosis (int id, double lum_interval, double cct_interval) {
        // setup light
        light.setId(id);
        dim_lights.add(light);

        // setup interval
        this.lum_interval = lum_interval;
        this.cct_interval = cct_interval;
    }

    public void run() {
        SocketClient.dimAllByLumCct(0, 3000);

        for (double cct = min_cct; cct <= max_cct; cct += cct_interval) {
            // repetition by cct
            for (double lum = 0; lum <= 100; lum += lum_interval) {
                // repetition by lum pct
                light.setLumPct(lum);
                light.setTemperature(cct);
                if(label_current != null) {
                    label_current.setText("LumPct: " + lum + " %, C.C.T.: " + cct);
                }
                dimAndGet(lum, cct);
            }
        }

        if(label_current != null) {
            label_current.setText("Diagnose succeed.");
        }
        SocketClient.dimAllByLumCct(10, 3000);
    }

    public void setProgress(ProgressBar progress, Label progress_label) {
    }

    public void setCurrentLabel(Label l) {
        label_current = l;
    }

    private void dimAndGet(double lum, double cct) {
        int ill_data = 0, tmp_data = 0;

        // dim
        SocketClient.dimByLights(dim_lights);

        // wait
        try {
            Thread.sleep(time_interval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // get data from sensor
        try {
            File file_ill = new File("illuminance.txt");
            File file_tmp = new File("temperature.txt");
            FileReader fr_ill = new FileReader(file_ill);
            FileReader fr_tmp = new FileReader(file_tmp);
            BufferedReader br_ill = new BufferedReader(fr_ill);
            BufferedReader br_tmp = new BufferedReader(fr_tmp);

            ill_data = Integer.parseInt(br_ill.readLine());
            tmp_data = Integer.parseInt(br_tmp.readLine());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // write out
        try {
            File file_out = new File("diagnosis.csv");
            FileWriter fw = new FileWriter(file_out, true);
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            pw.println(cct + "," + lum + "," + ill_data + "," + tmp_data);
            pw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
