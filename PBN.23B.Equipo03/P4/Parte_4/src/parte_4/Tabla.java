package parte_4;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;


public class Tabla extends javax.swing.JFrame {
    DefaultTableModel diseño=new DefaultTableModel();
    static String rutaArchivo = " ";
    //METODO PARA LLENAR LA TABLA
    void Llenado(){
        for (Linea auxiliar : Parte_4.LineasASM) {
            diseño.addRow(new Object[]{auxiliar.getConloc(),auxiliar.getEtiqueta(),auxiliar.getCodop(),auxiliar.getOperando(),auxiliar.getADDR(),
                                        auxiliar.getSize(),auxiliar.getCop()});
        }
    } //fin de metodo llenar 
    
    //metodo para elegir archivo
   public static String chooseFile() {
    JFileChooser fileChooser = new JFileChooser(); //variable para elegir
    fileChooser.setDialogTitle("Elige el asm");

    // Filtro para buscar solo ciertas extensiones, en este caso que sean .asm
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos de texto (*.asm)", "asm"); //busca solo asm
    fileChooser.setFileFilter(filter); // Se aplica el filtro. si hay otra extencion la ignora

    int returnValue = fileChooser.showOpenDialog(null); // Abre el cuadro de diálogo para seleccionar
//pasa a hacer valida la eleccion...
    if (returnValue == JFileChooser.APPROVE_OPTION) {//analiza si es un archivo asm
        File selectedFile = fileChooser.getSelectedFile();//agarra la direccion
        // Verificar si la extensión del archivo seleccionado es .asm
        if (selectedFile.getName().toLowerCase().endsWith(".asm")) {//toma el nombre y lo pasa a minusculas
            String selectedFilePath = selectedFile.getAbsolutePath(); // Guarda la dirección del archivo
            return selectedFilePath; // Retorna la dirección del archivo
        } else {//si no valida que es asm entonces...
            JOptionPane.showMessageDialog(null, "Selecciona un archivo con la extensión .asm", "Error", JOptionPane.ERROR_MESSAGE);
            //no lo toma y te manda notificacion en una notificacion emergente.
            return null; // Si el archivo no tiene la extensión .asm, no se selecciona
        }
    } else if (returnValue == JFileChooser.CANCEL_OPTION) {//en caso de no seleccionar nada...
        JOptionPane.showMessageDialog(null, "No has seleccionado ningún archivo", "Error", JOptionPane.ERROR_MESSAGE);
        return null;
        //manda la alerta de que no seleccionaste nada y se queda con el mismo archivo
    } else {
        return null; // Otros casos
    }

    }//Fin seleccionar el archivo
    
    public static void mostrarEnJTextArea() {
        // Limpiamos el JTextArea antes de mostrar nuevos datos
        Errores.setText("");

        // Iteramos sobre el ArrayList y agregamos cada elemento al JTextArea
        for (String elemento : Parte_4.Errores) {
            Errores.append(elemento + "\n"); // Agregamos un salto de línea después de cada elemento
        }
    }
    
    public Tabla() {
        initComponents();
        String[] titulo = new String[]{"CONLOC","ETQ","CODOP","OPR","ADDR","SIZE","COP"};
        diseño.setColumnIdentifiers(titulo);
        TablaCod.setModel(diseño);
        Llenado();
        Errores.setEditable(false);
        mostrarEnJTextArea();
        // Centrar todas las celdas de la tabla
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
        for (int i = 0; i < TablaCod.getColumnCount(); i++) {
            TablaCod.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TablaCod = new javax.swing.JTable();
        btnArchivo = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        LabelErrores = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        Errores = new javax.swing.JTextArea();
        Recargar = new javax.swing.JToggleButton();
        jLabel2 = new javax.swing.JLabel();

        jButton1.setText("jButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TablaCod = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int column) {
                // Devuelve false para hacer que todas las celdas sean de solo lectura
                return false;
            }
        };
        TablaCod.setBackground(new java.awt.Color(204, 255, 255));
        TablaCod.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        TablaCod.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        TablaCod.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        TablaCod.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        TablaCod.setSelectionBackground(new java.awt.Color(0, 204, 204));
        jScrollPane1.setViewportView(TablaCod);

        btnArchivo.setText("Abrir");
        btnArchivo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnArchivoActionPerformed(evt);
            }
        });

        jLabel1.setText("Abrir archivo distinto -->");

        LabelErrores.setText("Errores:");

        Errores.setColumns(20);
        Errores.setRows(5);
        jScrollPane2.setViewportView(Errores);

        Recargar.setText("Recargar");
        Recargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RecargarActionPerformed(evt);
            }
        });

        jLabel2.setText("Archivo actual - >");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 850, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane2)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(LabelErrores)
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnArchivo))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(Recargar)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnArchivo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(LabelErrores)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Recargar)
                    .addComponent(jLabel2))
                .addGap(7, 7, 7))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnArchivoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnArchivoActionPerformed
        rutaArchivo=chooseFile();
        if (rutaArchivo != null) {
            //Realizar de nuevo el proceso
            Parte_4.Fase1(rutaArchivo);
            if (Parte_4.LineasASM.size() != 0) {
                Fase2.fase2();
            }
            this.dispose();
            new Tabla().setVisible(true);
        }
    }//GEN-LAST:event_btnArchivoActionPerformed

    private void RecargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RecargarActionPerformed
        if(rutaArchivo==null){
            rutaArchivo=" ";
        }
        //Realizar de nuevo el proceso
        Parte_4.Fase1(rutaArchivo);
        if (Parte_4.LineasASM.size() != 0) {
            Fase2.fase2();
        }
        this.dispose();
        new Tabla().setVisible(true);
    }//GEN-LAST:event_RecargarActionPerformed

    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private static javax.swing.JTextArea Errores;
    private javax.swing.JLabel LabelErrores;
    private javax.swing.JToggleButton Recargar;
    private javax.swing.JTable TablaCod;
    private javax.swing.JButton btnArchivo;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
