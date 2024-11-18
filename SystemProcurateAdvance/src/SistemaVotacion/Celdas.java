/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion;

import java.awt.Component;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author jhdap
 */
public class Celdas extends DefaultTableCellRenderer {

    private ImageIcon winnerIcon;

    public Celdas() {
        winnerIcon = new ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/throphy.png"));
        Image img = winnerIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        winnerIcon = new ImageIcon(img);

    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        JLabel label = (JLabel) super.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, column);

        if (row == 0 && column == 0) {
            label.setIcon(winnerIcon);

            label.setIconTextGap(10);
        } else {
            label.setIcon(null);
        }

        return label;
    }

}
