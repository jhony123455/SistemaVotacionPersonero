/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion;

import static java.lang.Thread.sleep;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JLabel;

/**
 *
 * @author HP
 */
  public class Reloj extends Thread {

    private JLabel label;

    public Reloj(JLabel lbreloj) {
        this.label = lbreloj;
    }

    public void run() {
        while (true) {
            Date date = new Date();
            SimpleDateFormat formatDate = new SimpleDateFormat("hh:mm:ss");
            label.setText("" + formatDate.format(date));
            try {
                sleep(1000);

            } catch (Exception e) {

            }
        }
    }
}

