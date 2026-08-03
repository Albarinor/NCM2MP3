/*
 * Created by JFormDesigner on Sat Jan 23 11:01:06 CST 2021
 * Path persistence and UI safety improvements added 2026.
 */

package view;

import com.formdev.flatlaf.FlatIntelliJLaf;
import executor.AsyncTaskExecutor;
import executor.ConvertTask;
import utils.Utils;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Main application window for NCM2MP3.
 *
 * @author charlottexiao
 */
public class View extends JFrame {

    /** Persists last-used directories across restarts. */
    private final ViewPreferences viewPreferences = new ViewPreferences();

    public View() {
        FlatIntelliJLaf.setup();
        initComponents();
    }

    // ------------------------------------------------------------------ //
    // Button handlers                                                      //
    // ------------------------------------------------------------------ //

    /** "选择文件" — let the user pick NCM files / folders to convert. */
    private void button1ActionPerformed(ActionEvent e) {
        // Restore last directory before opening the dialog
        String lastInput = viewPreferences.getLastInputDir();
        if (lastInput != null) {
            jFileChooser1.setCurrentDirectory(new File(lastInput));
        }

        int returnVal = jFileChooser1.showOpenDialog(panel);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File[] selected = jFileChooser1.getSelectedFiles();
        if (selected == null || selected.length == 0) {
            return;
        }

        // Remember the directory of the first selection
        viewPreferences.saveLastInputDir(selected[0]);

        // Collect already-tracked paths to prevent duplicate rows
        Set<String> existingPaths = new HashSet<>();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            existingPaths.add((String) model.getValueAt(i, 1));
        }

        // Expand folders recursively
        ArrayList<File> files = new ArrayList<>();
        for (File file : selected) {
            Utils.listAllFiles(files, file);
        }

        // Add only files not already in the table
        for (File file : files) {
            if (!existingPaths.contains(file.getAbsolutePath())) {
                model.addRow(new String[]{
                        file.getName(),
                        file.getAbsolutePath(),
                        String.valueOf(file.length()),
                        "准备转换"
                });
            }
        }
    }

    /** "开始转换" — choose output directory and dispatch conversion tasks. */
    private void button2ActionPerformed(ActionEvent e) {
        // Restore last output directory
        String lastOutput = viewPreferences.getLastOutputDir();
        if (lastOutput != null) {
            jFileChooser2.setCurrentDirectory(new File(lastOutput));
        }

        int returnVal = jFileChooser2.showOpenDialog(panel);
        if (returnVal != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File outDir = jFileChooser2.getSelectedFile();
        if (outDir == null) {
            return;
        }

        if (!outDir.exists() && !outDir.mkdirs()) {
            JOptionPane.showMessageDialog(this, "无法创建保存目录：" + outDir, "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Persist the chosen output directory
        viewPreferences.saveLastOutputDir(outDir);

        String outFilePath = outDir.getAbsolutePath();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if ("准备转换".equals(model.getValueAt(i, 3))) {
                String ncmFilePath = (String) model.getValueAt(i, 1);
                AsyncTaskExecutor.submit(new ConvertTask(ncmFilePath, outFilePath, model, i));
            }
        }
    }

    /** "清空列表" — remove all rows from the table. */
    private void button3ActionPerformed(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }

    // ------------------------------------------------------------------ //
    // Component initialisation (generated structure kept intact)          //
    // ------------------------------------------------------------------ //

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - unknown
        panel = new JPanel();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        scrollPane = new JScrollPane();
        table = new JTable();

        //======== this ========
        setIconImage(new ImageIcon(Objects.requireNonNull(View.class.getResource("/image/ico.png"))).getImage());
        setTitle("NCM2MP3");
        setMinimumSize(null);
        setVisible(true);
        setFont(new Font("Microsoft YaHei UI", Font.PLAIN, 20));
        setResizable(false);
        setMaximizedBounds(null);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel ========
        {
            panel.setBorder(new javax.swing.border.CompoundBorder(new javax.swing.border.TitledBorder(new javax.swing.
                    border.EmptyBorder(0, 0, 0, 0), "author:charlottexiao", javax.swing.border.TitledBorder.CENTER
                    , javax.swing.border.TitledBorder.BOTTOM, new Font("Dialog", Font
                    .BOLD, 12), Color.red), panel.getBorder()));
            panel.addPropertyChangeListener(
                    e -> {
                        if ("border"
                                .equals(e.getPropertyName())) throw new RuntimeException();
                    });
            panel.setLayout(new GridLayout(1, 3, 2, 2));

            //---- button1 ----
            button1.setText("选择文件");
            // Use ActionListener instead of MouseListener for correct keyboard/accessibility behaviour
            button1.addActionListener(this::button1ActionPerformed);
            panel.add(button1);

            //---- button2 ----
            button2.setText("开始转换");
            button2.addActionListener(this::button2ActionPerformed);
            panel.add(button2);

            //---- button3 ----
            button3.setText("清空列表");
            button3.addActionListener(this::button3ActionPerformed);
            panel.add(button3);
        }
        contentPane.add(panel, BorderLayout.SOUTH);

        //======== scrollPane ========
        {
            scrollPane.setPreferredSize(new Dimension(700, 300));

            //---- table ----
            table.setPreferredSize(null);
            table.setPreferredScrollableViewportSize(new Dimension(0, 0));
            table.setModel(new DefaultTableModel(
                    new Object[][]{
                    },
                    new String[]{
                            "音乐名", "文件路径", "文件大小", "状态"
                    }
            ) {
                Class<?>[] columnTypes = new Class<?>[]{
                        String.class, String.class, String.class, String.class
                };

                @Override
                public Class<?> getColumnClass(int columnIndex) {
                    return columnTypes[columnIndex];
                }
            });
            {
                TableColumnModel cm = table.getColumnModel();
                cm.getColumn(0).setPreferredWidth(110);
                cm.getColumn(1).setPreferredWidth(400);
                cm.getColumn(2).setPreferredWidth(80);
                cm.getColumn(3).setPreferredWidth(70);
            }
            table.setRowHeight(30);
            table.setRowMargin(3);
            table.setAutoCreateRowSorter(true);
            table.setFillsViewportHeight(true);
            table.setOpaque(false);
            table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            table.setFocusable(false);
            table.setEnabled(false);
            scrollPane.setViewportView(table);
        }
        contentPane.add(scrollPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents

        // JFormChooser1 — input file/folder picker
        jFileChooser1 = new JFileChooser();
        jFileChooser1.setDialogTitle("请选择NCM音乐文件或文件夹");
        jFileChooser1.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jFileChooser1.setMultiSelectionEnabled(true);
        jFileChooser1.setFileFilter(new FileNameExtensionFilter("网易云NCM格式音乐", "ncm"));

        // JFormChooser2 — output directory picker
        jFileChooser2 = new JFileChooser();
        jFileChooser2.setDialogTitle("请选择保存目录");
        jFileChooser2.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        jFileChooser2.setMultiSelectionEnabled(false);

        // Table header is fixed (no reordering)
        table.getTableHeader().setReorderingAllowed(false);

        // Exit the process when the window is closed
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - unknown
    private JPanel panel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JScrollPane scrollPane;
    private JTable table;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    private JFileChooser jFileChooser1;
    private JFileChooser jFileChooser2;
}
