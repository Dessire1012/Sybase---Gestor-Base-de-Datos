/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sybasejconnect;

import java.awt.Color;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 *
 * @author dessi
 */
public class Base extends javax.swing.JFrame {
   
    public Base(String usuario, String contraseña) throws SQLException {

        initComponents();
        
        nombre = usuario;
        clave = contraseña;
        id = "";
        modelo = (DefaultListModel) Usuario.getModel();
        
        roles = new ArrayList();
        privilegios = new ArrayList();
      
        con = DriverManager.getConnection("jdbc:sybase:Tds:localhost:2638", "dessi", "dessi");
        st = con.createStatement();
        query = "select * from Usuario";
        rs = st.executeQuery(query);
        while (rs.next()){
            if(rs.getString(2).equalsIgnoreCase(nombre)){
                id = rs.getString(1);
            }  
        }
        
        query = "Select r1.Id_Rol, r1.Nombre from Usuario_Rol u1\n" +
                "inner join Rol r1 on u1.Id_Rol = r1.Id_Rol\n" +
                "inner join Usuario u2 on u2.Id_Usuario = u1.Id_Usuario and u2.Id_Usuario =  "+id;
        rs = st.executeQuery(query);
        while (rs.next()){
            roles.add(rs.getString(2));
        }
        
        for(int i=0; i<roles.size(); i++){
            query = "Select p1.Id_Privilegio, p1.Nombre from Rol_Privilegio r1\n" +
                    "inner join Privilegio p1 on p1.Id_Privilegio = r1.Id_Privilegio\n" + 
                    "inner join Rol r2 on r1.Id_Rol = r2.Id_Rol and r2.Nombre = '" +roles.get(i)+"'";
        rs = st.executeQuery(query);
            while (rs.next()){
                privilegios.add(rs.getString(2));
            }
        }
        
        LlenarLista("Usuario");
        LlenarLista("Rol");
        LlenarLista("Privilegio");

        NombreUsuario.setText(nombre);
        IdUsuario.setText(id);       
    }

    private Base() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void LlenarLista(String nombreLista) throws SQLException{
            con = DriverManager.getConnection("jdbc:sybase:Tds:localhost:2638", "dessi", "dessi");
            st = con.createStatement();
            query = "";
            
            if(nombreLista.equalsIgnoreCase("usuario")){
                query = "select * from Usuario";
                 modelo = (DefaultListModel) Usuario.getModel();
                 modelo.removeAllElements();
                 rs = st.executeQuery(query);
                while (rs.next()){
                    modelo.addElement(rs.getString(1) + "-"+ rs.getString(2));
                }
            }
            else if (nombreLista.equalsIgnoreCase("rol")){
                 modelo = (DefaultListModel) Rol.getModel();
                 modelo.removeAllElements();
                query = "select * from Rol";
                rs = st.executeQuery(query);
                while (rs.next()){
                    modelo.addElement(rs.getString(1) + "-"+ rs.getString(2));
                }
            }else if (nombreLista.equalsIgnoreCase("privilegio")){
                 modelo = (DefaultListModel) Privilegio.getModel();
                 modelo.removeAllElements();
                query = "select * from Privilegio";
                rs = st.executeQuery(query);
                while (rs.next()){
                    modelo.addElement(rs.getString(1) + "-"+ rs.getString(2));
                }
            }
        
    }
    
    void LlenarTabla(String nombrePanel) throws SQLException{
        model = (DefaultTableModel)jTable1.getModel();

        for (int i = 0; i < jTable1.getRowCount(); i++) {
            model.removeRow(i);
            i-=1;
        }
        
            con = DriverManager.getConnection("jdbc:sybase:Tds:localhost:2638", "dessi", "dessi");
            st = con.createStatement();
            query = "select * from " + nombrePanel;
            rs = st.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            
            int cols = rsmd.getColumnCount();
            String[] colNombre = new String [cols];
            for(int i=0; i < cols; i++){
                colNombre[i]=rsmd.getColumnName(i+1);
            }
            model.setColumnIdentifiers(colNombre);
            
            Object[] row = new Object [cols];
            
            if (rs.next()){
                rs = st.executeQuery(query);
                while (rs.next()){
                   for(int i=0; i < cols; i++){
                        row[i]=rs.getString(i+1);
                   }
                   model.addRow(row);
                }
                for(int i=0; i < cols; i++){
                   row[i]= "";
                }
                model.addRow(row);
            }
            else{
                for(int i=0; i < cols; i++){
                   row[i]= "";
                }
                model.addRow(row);
            }
            
            for (int i = 0; i < roles.size(); i++) {
                if(roles.get(i).equalsIgnoreCase("Jefe")){
                    if(nombrePanel.equalsIgnoreCase("ajuste")|| nombrePanel.equalsIgnoreCase("detalle_ajuste"))
                        jTable1.setEnabled(true);
                    else
                        jTable1.setEnabled(false);

                }
                else if (roles.get(i).equalsIgnoreCase("Vendedor")){
                    if(nombrePanel.equalsIgnoreCase("factura") || nombrePanel.equalsIgnoreCase("detalle_factura"))
                        jTable1.setEnabled(true);
                    else
                        jTable1.setEnabled(false);  
                }      
                else if (roles.get(i).equalsIgnoreCase("Compador")){
                    if(nombrePanel.equalsIgnoreCase("compra") || nombrePanel.equalsIgnoreCase("detalle_compra"))
                        jTable1.setEnabled(true);
                    else
                        jTable1.setEnabled(false);
                }
                else if (roles.get(i).equalsIgnoreCase("admin")){
                    jTable1.setEnabled(true);
                    break;
                }              
            }
            if (nombrePanel.equalsIgnoreCase("Kardex")){
                jTable1.setEnabled(false);
            }
    }

    void setLastPanel (String nombre){
        nombreLastPanel = nombre;
    }
    
    String getLastPanel (){
        return nombreLastPanel;
    }
    
    void setColor(JPanel panel){
        panel.setBackground(new Color (252,172,224));
    }
    
    void resetColor(JPanel panel){
        panel.setBackground(new Color (249,106,193));
    }
    
    void setLastList (String nombre){
        nombreLastList = nombre;
    }
    
    String getLastList (){
        return nombreLastList;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tabla = new javax.swing.JPopupMenu();
        Agregar = new javax.swing.JMenuItem();
        Actualizar = new javax.swing.JMenuItem();
        Eliminar = new javax.swing.JMenuItem();
        Seguridad = new javax.swing.JPopupMenu();
        Ver = new javax.swing.JMenuItem();
        Roles = new javax.swing.JMenuItem();
        Privilegios = new javax.swing.JMenuItem();
        Eliminar1 = new javax.swing.JMenuItem();
        jPanel3 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        Producto = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        Compra = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        Ajuste = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        Detalle_Ajuste = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        Factura = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        Detalle_Factura = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        Cliente = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        Proveedor = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        Kardex = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        Detalle_Compra = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Rol = new javax.swing.JList<>();
        jScrollPane3 = new javax.swing.JScrollPane();
        Usuario = new javax.swing.JList<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        Privilegio = new javax.swing.JList<>();
        Crear_nuevo_rol = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        Crear_Nuevo_Usuario = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        Crear_nuevo_privilegio = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel13 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jPanel14 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jPanel16 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        IdUsuario = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        NombreUsuario = new javax.swing.JLabel();

        Tabla.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N

        Agregar.setText("Agregar");
        Agregar.setEnabled(false);
        Agregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AgregarActionPerformed(evt);
            }
        });
        Tabla.add(Agregar);

        Actualizar.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Actualizar.setText("Actualizar");
        Actualizar.setEnabled(false);
        Actualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ActualizarActionPerformed(evt);
            }
        });
        Tabla.add(Actualizar);

        Eliminar.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Eliminar.setText("Eliminar");
        Eliminar.setEnabled(false);
        Eliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EliminarActionPerformed(evt);
            }
        });
        Tabla.add(Eliminar);

        Seguridad.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N

        Ver.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Ver.setText("Ver");
        Ver.setEnabled(false);
        Ver.setName(""); // NOI18N
        Ver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                VerActionPerformed(evt);
            }
        });
        Seguridad.add(Ver);

        Roles.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Roles.setText("Roles");
        Roles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RolesActionPerformed(evt);
            }
        });
        Seguridad.add(Roles);

        Privilegios.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Privilegios.setText("Privilegios");
        Privilegios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                PrivilegiosActionPerformed(evt);
            }
        });
        Seguridad.add(Privilegios);

        Eliminar1.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N
        Eliminar1.setText("Eliminar");
        Eliminar1.setEnabled(false);
        Eliminar1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Eliminar1ActionPerformed(evt);
            }
        });
        Seguridad.add(Eliminar1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setForeground(new java.awt.Color(255, 255, 255));

        jPanel4.setBackground(new java.awt.Color(249, 106, 193));

        jLabel3.setFont(new java.awt.Font("Roboto", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("Tecnología 123");
        jLabel3.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jLabel3.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane2.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane2.setFont(new java.awt.Font("Roboto Medium", 0, 13)); // NOI18N
        jTabbedPane2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTabbedPane2MouseClicked(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.setFont(new java.awt.Font("Roboto", 1, 13)); // NOI18N

        jScrollPane2.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane2.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));
        jScrollPane2.setFont(new java.awt.Font("Roboto", 0, 15)); // NOI18N

        jTable1 = new javax.swing.JTable(){
            public boolean isCellEditable(int row, int col){
                DefaultTableModel modelo = (DefaultTableModel) jTable1.getModel();

                if (model.getColumnName(6).equalsIgnoreCase("Código_Producto"))
                return true;
                else if (col == (modelo.getColumnCount()-1)
                    || col == (modelo.getColumnCount()-2) || col == (modelo.getColumnCount()-3)
                    || col == (modelo.getColumnCount()-4))
                return false;
                else
                return true;
            }
        };
        jTable1.setAutoCreateRowSorter(true);
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTable1.setEnabled(false);
        jTable1.setGridColor(new java.awt.Color(255, 255, 255));
        jTable1.setSelectionBackground(new java.awt.Color(252, 172, 224));
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable1);

        jPanel6.setBackground(new java.awt.Color(249, 106, 193));

        Producto.setBackground(new java.awt.Color(252, 172, 224));
        Producto.setName("Producto"); // NOI18N
        Producto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProductoMouseClicked(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel2.setText("Producto");

        javax.swing.GroupLayout ProductoLayout = new javax.swing.GroupLayout(Producto);
        Producto.setLayout(ProductoLayout);
        ProductoLayout.setHorizontalGroup(
            ProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(ProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ProductoLayout.createSequentialGroup()
                    .addGap(0, 41, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addGap(0, 42, Short.MAX_VALUE)))
        );
        ProductoLayout.setVerticalGroup(
            ProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(ProductoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ProductoLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Compra.setBackground(new java.awt.Color(249, 106, 193));
        Compra.setName("Compra"); // NOI18N
        Compra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CompraMouseClicked(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel4.setText("Compra");

        javax.swing.GroupLayout CompraLayout = new javax.swing.GroupLayout(Compra);
        Compra.setLayout(CompraLayout);
        CompraLayout.setHorizontalGroup(
            CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(CompraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );
        CompraLayout.setVerticalGroup(
            CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(CompraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel4)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Ajuste.setBackground(new java.awt.Color(249, 106, 193));
        Ajuste.setName("Ajuste"); // NOI18N
        Ajuste.setPreferredSize(new java.awt.Dimension(84, 30));
        Ajuste.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                AjusteMouseClicked(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel5.setText("Ajuste");

        javax.swing.GroupLayout AjusteLayout = new javax.swing.GroupLayout(Ajuste);
        Ajuste.setLayout(AjusteLayout);
        AjusteLayout.setHorizontalGroup(
            AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 146, Short.MAX_VALUE)
            .addGroup(AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AjusteLayout.createSequentialGroup()
                    .addGap(0, 22, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addGap(0, 22, Short.MAX_VALUE)))
        );
        AjusteLayout.setVerticalGroup(
            AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(AjusteLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel5)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Detalle_Ajuste.setBackground(new java.awt.Color(249, 106, 193));
        Detalle_Ajuste.setName("Detalle_Ajuste"); // NOI18N
        Detalle_Ajuste.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Detalle_AjusteMouseClicked(evt);
            }
        });

        jLabel6.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel6.setText("Detalle Ajuste");

        javax.swing.GroupLayout Detalle_AjusteLayout = new javax.swing.GroupLayout(Detalle_Ajuste);
        Detalle_Ajuste.setLayout(Detalle_AjusteLayout);
        Detalle_AjusteLayout.setHorizontalGroup(
            Detalle_AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Detalle_AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_AjusteLayout.createSequentialGroup()
                    .addGap(0, 16, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addGap(0, 16, Short.MAX_VALUE)))
        );
        Detalle_AjusteLayout.setVerticalGroup(
            Detalle_AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(Detalle_AjusteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_AjusteLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel6)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Factura.setBackground(new java.awt.Color(249, 106, 193));
        Factura.setName("Factura"); // NOI18N
        Factura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                FacturaMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel8.setText("Factura");

        javax.swing.GroupLayout FacturaLayout = new javax.swing.GroupLayout(Factura);
        Factura.setLayout(FacturaLayout);
        FacturaLayout.setHorizontalGroup(
            FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(FacturaLayout.createSequentialGroup()
                    .addGap(0, 22, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 22, Short.MAX_VALUE)))
        );
        FacturaLayout.setVerticalGroup(
            FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(FacturaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel8)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Detalle_Factura.setBackground(new java.awt.Color(249, 106, 193));
        Detalle_Factura.setName("Detalle_Factura"); // NOI18N
        Detalle_Factura.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Detalle_FacturaMouseClicked(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel9.setText("Detalle Factura");

        javax.swing.GroupLayout Detalle_FacturaLayout = new javax.swing.GroupLayout(Detalle_Factura);
        Detalle_Factura.setLayout(Detalle_FacturaLayout);
        Detalle_FacturaLayout.setHorizontalGroup(
            Detalle_FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Detalle_FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_FacturaLayout.createSequentialGroup()
                    .addGap(0, 22, Short.MAX_VALUE)
                    .addComponent(jLabel9)
                    .addGap(0, 22, Short.MAX_VALUE)))
        );
        Detalle_FacturaLayout.setVerticalGroup(
            Detalle_FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(Detalle_FacturaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_FacturaLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel9)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Cliente.setBackground(new java.awt.Color(249, 106, 193));
        Cliente.setName("Cliente"); // NOI18N
        Cliente.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ClienteMouseClicked(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel10.setText("Cliente");

        javax.swing.GroupLayout ClienteLayout = new javax.swing.GroupLayout(Cliente);
        Cliente.setLayout(ClienteLayout);
        ClienteLayout.setHorizontalGroup(
            ClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(ClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ClienteLayout.createSequentialGroup()
                    .addGap(0, 22, Short.MAX_VALUE)
                    .addComponent(jLabel10)
                    .addGap(0, 22, Short.MAX_VALUE)))
        );
        ClienteLayout.setVerticalGroup(
            ClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(ClienteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ClienteLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel10)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Proveedor.setBackground(new java.awt.Color(249, 106, 193));
        Proveedor.setName("Proveedor"); // NOI18N
        Proveedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ProveedorMouseClicked(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel11.setText("Proveedor");

        javax.swing.GroupLayout ProveedorLayout = new javax.swing.GroupLayout(Proveedor);
        Proveedor.setLayout(ProveedorLayout);
        ProveedorLayout.setHorizontalGroup(
            ProveedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(ProveedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ProveedorLayout.createSequentialGroup()
                    .addGap(0, 40, Short.MAX_VALUE)
                    .addComponent(jLabel11)
                    .addGap(0, 40, Short.MAX_VALUE)))
        );
        ProveedorLayout.setVerticalGroup(
            ProveedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(ProveedorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(ProveedorLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel11)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Kardex.setBackground(new java.awt.Color(249, 106, 193));
        Kardex.setName("Kardex"); // NOI18N
        Kardex.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                KardexMouseClicked(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel12.setText("Kardex");

        javax.swing.GroupLayout KardexLayout = new javax.swing.GroupLayout(Kardex);
        Kardex.setLayout(KardexLayout);
        KardexLayout.setHorizontalGroup(
            KardexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(KardexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KardexLayout.createSequentialGroup()
                    .addGap(0, 49, Short.MAX_VALUE)
                    .addComponent(jLabel12)
                    .addGap(0, 49, Short.MAX_VALUE)))
        );
        KardexLayout.setVerticalGroup(
            KardexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(KardexLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(KardexLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel12)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        Detalle_Compra.setBackground(new java.awt.Color(249, 106, 193));
        Detalle_Compra.setName("Detalle_Compra"); // NOI18N
        Detalle_Compra.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Detalle_CompraMouseClicked(evt);
            }
        });

        jLabel13.setFont(new java.awt.Font("Roboto Thin", 0, 15)); // NOI18N
        jLabel13.setText("Detalle Compra");

        javax.swing.GroupLayout Detalle_CompraLayout = new javax.swing.GroupLayout(Detalle_Compra);
        Detalle_Compra.setLayout(Detalle_CompraLayout);
        Detalle_CompraLayout.setHorizontalGroup(
            Detalle_CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(Detalle_CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_CompraLayout.createSequentialGroup()
                    .addGap(0, 20, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addGap(0, 21, Short.MAX_VALUE)))
        );
        Detalle_CompraLayout.setVerticalGroup(
            Detalle_CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(Detalle_CompraLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(Detalle_CompraLayout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel13)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Compra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Ajuste, javax.swing.GroupLayout.DEFAULT_SIZE, 146, Short.MAX_VALUE)
            .addComponent(Factura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Detalle_Ajuste, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Detalle_Factura, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Cliente, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Proveedor, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Kardex, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Producto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(Detalle_Compra, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(80, 80, 80)
                .addComponent(Producto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Detalle_Compra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(Ajuste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Detalle_Ajuste, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Detalle_Factura, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Proveedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Kardex, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1166, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 739, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Base", jPanel1);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setFont(new java.awt.Font("Roboto", 1, 13)); // NOI18N

        jLabel22.setFont(new java.awt.Font("Roboto", 1, 18)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(151, 151, 231));
        jLabel22.setText("Usuarios");

        jLabel23.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(151, 151, 231));
        jLabel23.setText("Roles");

        jLabel24.setFont(new java.awt.Font("Roboto", 1, 20)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(151, 151, 231));
        jLabel24.setText("Privilegios");

        Rol.setFont(new java.awt.Font("Roboto Light", 0, 15)); // NOI18N
        Rol.setModel(new DefaultListModel ());
        Rol.setSelectionBackground(new java.awt.Color(151, 151, 231));
        Rol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                RolMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(Rol);

        Usuario.setFont(new java.awt.Font("Roboto Light", 0, 15)); // NOI18N
        Usuario.setModel(new DefaultListModel ());
        Usuario.setSelectionBackground(new java.awt.Color(151, 151, 231));
        Usuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                UsuarioMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(Usuario);

        Privilegio.setFont(new java.awt.Font("Roboto Light", 0, 15)); // NOI18N
        Privilegio.setModel(new DefaultListModel ());
        Privilegio.setSelectionBackground(new java.awt.Color(151, 151, 231));
        Privilegio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PrivilegioMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(Privilegio);

        Crear_nuevo_rol.setBackground(new java.awt.Color(151, 151, 231));
        Crear_nuevo_rol.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Crear_nuevo_rolMouseClicked(evt);
            }
        });

        jLabel26.setFont(new java.awt.Font("Roboto", 1, 15)); // NOI18N
        jLabel26.setText("Crear nuevo rol");

        javax.swing.GroupLayout Crear_nuevo_rolLayout = new javax.swing.GroupLayout(Crear_nuevo_rol);
        Crear_nuevo_rol.setLayout(Crear_nuevo_rolLayout);
        Crear_nuevo_rolLayout.setHorizontalGroup(
            Crear_nuevo_rolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Crear_nuevo_rolLayout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addComponent(jLabel26)
                .addGap(48, 48, 48))
        );
        Crear_nuevo_rolLayout.setVerticalGroup(
            Crear_nuevo_rolLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Crear_nuevo_rolLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel26)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        Crear_Nuevo_Usuario.setBackground(new java.awt.Color(151, 151, 231));
        Crear_Nuevo_Usuario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Crear_Nuevo_UsuarioMouseClicked(evt);
            }
        });

        jLabel25.setFont(new java.awt.Font("Roboto", 1, 15)); // NOI18N
        jLabel25.setText("Crear nuevo usuario");

        javax.swing.GroupLayout Crear_Nuevo_UsuarioLayout = new javax.swing.GroupLayout(Crear_Nuevo_Usuario);
        Crear_Nuevo_Usuario.setLayout(Crear_Nuevo_UsuarioLayout);
        Crear_Nuevo_UsuarioLayout.setHorizontalGroup(
            Crear_Nuevo_UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Crear_Nuevo_UsuarioLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addGap(37, 37, 37))
        );
        Crear_Nuevo_UsuarioLayout.setVerticalGroup(
            Crear_Nuevo_UsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Crear_Nuevo_UsuarioLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel25)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        Crear_nuevo_privilegio.setBackground(new java.awt.Color(151, 151, 231));
        Crear_nuevo_privilegio.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Crear_nuevo_privilegioMouseClicked(evt);
            }
        });

        jLabel27.setFont(new java.awt.Font("Roboto", 1, 15)); // NOI18N
        jLabel27.setText("Crear nuevo privilegio");

        javax.swing.GroupLayout Crear_nuevo_privilegioLayout = new javax.swing.GroupLayout(Crear_nuevo_privilegio);
        Crear_nuevo_privilegio.setLayout(Crear_nuevo_privilegioLayout);
        Crear_nuevo_privilegioLayout.setHorizontalGroup(
            Crear_nuevo_privilegioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Crear_nuevo_privilegioLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(jLabel27)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        Crear_nuevo_privilegioLayout.setVerticalGroup(
            Crear_nuevo_privilegioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Crear_nuevo_privilegioLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel27)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(100, 100, 100)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(Crear_Nuevo_Usuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 278, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Crear_nuevo_rol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(256, 256, 256)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(Crear_nuevo_privilegio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(100, 100, 100))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(152, 152, 152)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel23)
                .addGap(386, 386, 386)
                .addComponent(jLabel24)
                .addGap(160, 160, 160))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 500, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane1))
                .addGap(31, 31, 31)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Crear_nuevo_rol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Crear_Nuevo_Usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Crear_nuevo_privilegio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );

        jTabbedPane2.addTab("Seguridad", jPanel2);

        jPanel8.setBackground(new java.awt.Color(255, 255, 255));

        jPanel9.setBackground(new java.awt.Color(198, 223, 249));

        jLabel14.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel14.setText("Reportes de existencia de productos ");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addGap(0, 260, Short.MAX_VALUE)
                    .addComponent(jLabel14)
                    .addGap(0, 261, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel14)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel10.setBackground(new java.awt.Color(127, 160, 240));

        jLabel16.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel16.setText("Tipos de reportes");

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(0, 272, Short.MAX_VALUE)
                    .addComponent(jLabel16)
                    .addGap(0, 273, Short.MAX_VALUE)))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel10Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel16)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel11.setBackground(new java.awt.Color(198, 223, 249));

        jLabel17.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel17.setText("Reportes de productos por debajo de la existencia mínima");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 190, Short.MAX_VALUE)
                    .addComponent(jLabel17)
                    .addGap(0, 190, Short.MAX_VALUE)))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel11Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel17)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel13.setBackground(new java.awt.Color(198, 223, 249));

        jLabel18.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel18.setText("Reportes de ventas por mes ");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(0, 289, Short.MAX_VALUE)
                    .addComponent(jLabel18)
                    .addGap(0, 290, Short.MAX_VALUE)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel13Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel18)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel14.setBackground(new java.awt.Color(198, 223, 249));

        jLabel19.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel19.setText("Reporte de compras por producto");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addGap(0, 272, Short.MAX_VALUE)
                    .addComponent(jLabel19)
                    .addGap(0, 273, Short.MAX_VALUE)))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel14Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel19)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jPanel16.setBackground(new java.awt.Color(127, 160, 240));

        jLabel21.setFont(new java.awt.Font("Roboto Medium", 0, 18)); // NOI18N
        jLabel21.setText("Opciones");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 470, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel16Layout.createSequentialGroup()
                    .addGap(0, 197, Short.MAX_VALUE)
                    .addComponent(jLabel21)
                    .addGap(0, 198, Short.MAX_VALUE)))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 30, Short.MAX_VALUE)
            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel16Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jLabel21)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        jButton1.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\Imagenes\\icons8-excel-24.png")); // NOI18N
        jButton1.setText("Excel");

        jButton2.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\Imagenes\\icons8-pdf-24.png")); // NOI18N
        jButton2.setText("PDF");
        jButton2.setToolTipText("");

        jButton3.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-excel-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton3.setText("Excel");

        jButton4.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-excel-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton4.setText("Excel");

        jButton5.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\icons8-excel-24.png")); // NOI18N
        jButton5.setText("Excel");

        jButton6.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-pdf-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton6.setText("PDF");

        jButton7.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-pdf-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton7.setText("PDF");

        jButton8.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\icons8-pdf-24.png")); // NOI18N
        jButton8.setText("PDF");

        jButton9.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\icons8-document-header-24.png")); // NOI18N
        jButton9.setText("Generar");
        jButton9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton9MouseClicked(evt);
            }
        });

        jButton10.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-document-header-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton10.setText("Generar");
        jButton10.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton10MouseClicked(evt);
            }
        });

        jButton11.setIcon(new javax.swing.JLabel() {
            public javax.swing.Icon getIcon() {
                try {
                    return new javax.swing.ImageIcon(
                        new java.net.URL("file:/C:/Users/dessi/OneDrive/Documentos/NetBeansProjects/SybaseJDBC/Imagenes/icons8-document-header-24.png")
                    );
                } catch (java.net.MalformedURLException e) {
                }
                return null;
            }
        }.getIcon());
        jButton11.setText("Generar");
        jButton11.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton11MouseClicked(evt);
            }
        });

        jButton12.setIcon(new javax.swing.ImageIcon("C:\\Users\\dessi\\OneDrive\\Documentos\\NetBeansProjects\\SybaseJDBC\\icons8-document-header-24.png")); // NOI18N
        jButton12.setText("Generar");
        jButton12.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jButton12MouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(40, 40, 40))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(68, 68, 68)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton9)
                            .addComponent(jButton10)
                            .addComponent(jButton11)
                            .addComponent(jButton12))
                        .addGap(69, 69, 69)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                                .addComponent(jButton7))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton5)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 84, Short.MAX_VALUE)
                                .addComponent(jButton8))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton6))
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jButton1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2)))
                        .addGap(61, 61, 61))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(70, 70, 70)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jButton1)
                                        .addComponent(jButton2)
                                        .addComponent(jButton9)))
                                .addGap(70, 70, 70)
                                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButton3)
                                .addComponent(jButton6)
                                .addComponent(jButton10)))
                        .addGap(70, 70, 70)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton4)
                        .addComponent(jButton7)
                        .addComponent(jButton11)))
                .addGap(71, 71, 71)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5)
                        .addComponent(jButton8)
                        .addComponent(jButton12)))
                .addContainerGap(232, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Reportes", jPanel8);

        jPanel5.setBackground(new java.awt.Color(252, 172, 224));

        jLabel1.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel1.setText("Id:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel1)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addContainerGap())
        );

        IdUsuario.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        IdUsuario.setForeground(new java.awt.Color(252, 172, 224));
        IdUsuario.setText("\"--\"");

        jPanel7.setBackground(new java.awt.Color(252, 172, 224));

        jLabel15.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        jLabel15.setText("Usuario:");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel15)
                .addContainerGap())
        );

        NombreUsuario.setFont(new java.awt.Font("Roboto Medium", 0, 15)); // NOI18N
        NombreUsuario.setForeground(new java.awt.Color(252, 172, 224));
        NombreUsuario.setText("\"--\"");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(IdUsuario)
                        .addGap(5, 5, 5)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addComponent(NombreUsuario))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(IdUsuario))
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(NombreUsuario)))
                .addGap(7, 7, 7)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        String nombreTabla = getLastPanel();
        if(evt.isMetaDown()){
            if(!nombreTabla.equalsIgnoreCase("kardex"))
                Tabla.show(evt.getComponent(),
                            evt.getX(), evt.getY()); 
        }
        for (int i = 0; i < privilegios.size(); i++) {
            if(privilegios.get(i).equalsIgnoreCase("eliminar"))
                    Eliminar.setEnabled(true);
            else if (privilegios.get(i).equalsIgnoreCase("crear"))
                    Agregar.setEnabled(true);
            else if (privilegios.get(i).equalsIgnoreCase("editar"))
                    Actualizar.setEnabled(true);
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void EliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EliminarActionPerformed

    String nombreTabla = getLastPanel();
    model = (DefaultTableModel)jTable1.getModel();
    id = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 0);
    String id_padreDetalle = (String) jTable1.getValueAt(jTable1.getSelectedRow(), 5);

            
    if(nombreTabla.equalsIgnoreCase("detalle_compra") || nombreTabla.equalsIgnoreCase("detalle_ajuste") ||
       nombreTabla.equalsIgnoreCase("detalle_factura") ){
       query = "DELETE FROM " + nombreTabla + "\n"+
                "Where Número_Detalle = "+id + " and " +jTable1.getColumnName(5)+ " = " + id_padreDetalle;

           try {
                st.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Eliminado con exito");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo Eliminar\n");
            }    
    }else{
         try {
                call = con.prepareCall("Exec sp_"+nombreTabla+"_delete("+id+")");
                call.executeUpdate();
                JOptionPane.showMessageDialog(this, "Eliminado con exito");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar\n");
            }         
    }
    try {      
                LlenarTabla(nombreTabla);    
            } catch (SQLException ex) {
                Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
            } 
    }//GEN-LAST:event_EliminarActionPerformed

    private void ActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ActualizarActionPerformed
        String nombreTabla = getLastPanel();
        id = IdUsuario.getText();
        model = (DefaultTableModel)jTable1.getModel();
        String update= "";

        if(nombreTabla.equalsIgnoreCase("detalle_compra") || nombreTabla.equalsIgnoreCase("detalle_ajuste") ||
           nombreTabla.equalsIgnoreCase("detalle_factura") ){
           
        }else{
            for(int i=0; i < model.getColumnCount()-4; i++){
            update += jTable1.getValueAt(jTable1.getSelectedRow(), i) + ", ";
            }
            
            update += id;

            try {
                call = con.prepareCall("Exec sp_"+nombreTabla+"_update("+update+")");
                call.executeUpdate();
                JOptionPane.showMessageDialog(this, "Actualizado con exito");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo actualizar\n");
            } 
        }
        try {      
                LlenarTabla(nombreTabla);    
            } catch (SQLException ex) {
                Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
            }

    }//GEN-LAST:event_ActualizarActionPerformed

    private void AgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AgregarActionPerformed
        String nombreTabla = getLastPanel();
        id = IdUsuario.getText();
        model = (DefaultTableModel)jTable1.getModel();
        String crear= "";
  
        System.out.println(crear);
        if(nombreTabla.equalsIgnoreCase("detalle_compra") || nombreTabla.equalsIgnoreCase("detalle_ajuste") ||
           nombreTabla.equalsIgnoreCase("detalle_factura") ){
           
            for(int i=0; i < model.getColumnCount(); i++){
                if(i+1==model.getColumnCount())
                    crear += jTable1.getValueAt(jTable1.getSelectedRow(), i);
                else if (jTable1.getValueAt(jTable1.getSelectedRow(), i) == "")
                    crear += "null,";
                else
                    crear += jTable1.getValueAt(jTable1.getSelectedRow(), i) + ",";
            }

           query = "Insert into " + nombreTabla + "\n"+
                    "Values ("+crear+")"; 

           try {
                st.executeUpdate(query);
                JOptionPane.showMessageDialog(this, "Creado con exito");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo crear\n");
            }
        }else{
            for(int i=0; i < model.getColumnCount()-4; i++){
            if(jTable1.getValueAt(jTable1.getSelectedRow(), i) == "" && nombreTabla.equalsIgnoreCase("producto") 
               || jTable1.getValueAt(jTable1.getSelectedRow(), i) == "" && nombreTabla.equalsIgnoreCase("factura") )
                crear += "";
            else if (jTable1.getValueAt(jTable1.getSelectedRow(), i) == ""){
                crear += "null,";
            }
            else 
                crear += jTable1.getValueAt(jTable1.getSelectedRow(), i) + ", ";
            }
            
            crear += id;
            
            try {
                call = con.prepareCall("Exec sp_"+nombreTabla+"_create("+crear+")");
                call.executeUpdate();
                JOptionPane.showMessageDialog(this, "Creado con exito");
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo crear\n" +ex);
            } 
        }
        
        try {      
                LlenarTabla(nombreTabla);    
            } catch (SQLException ex) {
                Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
            }
            
    
    }//GEN-LAST:event_AgregarActionPerformed

    private void CompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_CompraMouseClicked
        setColor(Compra);
        resetColor(Producto);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Compra.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Compra.getName());
    }//GEN-LAST:event_CompraMouseClicked

    private void AjusteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_AjusteMouseClicked
        setColor(Ajuste);
        resetColor(Compra);
        resetColor(Producto);
        resetColor(Detalle_Ajuste);
        resetColor(Detalle_Compra);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        
        try {
            LlenarTabla(Ajuste.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Ajuste.getName());
    }//GEN-LAST:event_AjusteMouseClicked

    private void Detalle_AjusteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Detalle_AjusteMouseClicked
        setColor(Detalle_Ajuste);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Producto);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Detalle_Ajuste.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Detalle_Ajuste.getName());
    }//GEN-LAST:event_Detalle_AjusteMouseClicked

    private void FacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_FacturaMouseClicked
        setColor(Factura);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Producto);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Factura.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Factura.getName());
    }//GEN-LAST:event_FacturaMouseClicked

    private void Detalle_FacturaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Detalle_FacturaMouseClicked
        setColor(Detalle_Factura);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Producto);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Detalle_Factura.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Detalle_Factura.getName());
    }//GEN-LAST:event_Detalle_FacturaMouseClicked

    private void ClienteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ClienteMouseClicked
        setColor(Cliente);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Producto);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Cliente.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Cliente.getName());
    }//GEN-LAST:event_ClienteMouseClicked

    private void ProveedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProveedorMouseClicked
        setColor(Proveedor);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Producto);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Proveedor.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Proveedor.getName());
    }//GEN-LAST:event_ProveedorMouseClicked

    private void KardexMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_KardexMouseClicked
        setColor(Kardex);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Producto);
        
        try {
            LlenarTabla(Kardex.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Kardex.getName());
    }//GEN-LAST:event_KardexMouseClicked

    private void Detalle_CompraMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Detalle_CompraMouseClicked
        setColor(Detalle_Compra);
        resetColor(Compra);
        resetColor(Producto);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Detalle_Compra.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Detalle_Compra.getName());
    }//GEN-LAST:event_Detalle_CompraMouseClicked

    private void ProductoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ProductoMouseClicked
        setColor(Producto);
        resetColor(Compra);
        resetColor(Detalle_Compra);
        resetColor(Ajuste);
        resetColor(Detalle_Ajuste);
        resetColor(Factura);
        resetColor(Detalle_Factura);
        resetColor(Cliente);
        resetColor(Proveedor);
        resetColor(Kardex);
        
        try {
            LlenarTabla(Producto.getName());
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        setLastPanel(Producto.getName());
    }//GEN-LAST:event_ProductoMouseClicked

    private void UsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_UsuarioMouseClicked
        if(evt.isMetaDown()){
            Seguridad.show(evt.getComponent(),
                        evt.getX(), evt.getY());
            Roles.setVisible(true);
            Privilegios.setVisible(false);
        }
        setLastList("Usuario");
        
        for (int i = 0; i < privilegios.size(); i++) {
            if(privilegios.get(i).equalsIgnoreCase("ver"))
                    Ver.setEnabled(true);
            else if (privilegios.get(i).equalsIgnoreCase("eliminar"))
                    Eliminar1.setEnabled(true);
        }
    }//GEN-LAST:event_UsuarioMouseClicked

    private void Eliminar1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Eliminar1ActionPerformed
        String lista = "";
        if(getLastList().equalsIgnoreCase("usuario")){
            item = Usuario.getSelectedValue(); 
            lista = "usuario";
        }    
        else if (getLastList().equalsIgnoreCase("rol")){
            item = Rol.getSelectedValue();
            lista = "rol";
        }    
        else if (getLastList().equalsIgnoreCase("privilegio")){
            item = Privilegio.getSelectedValue();
            lista = "privilegio";
        }
        
        int index = item.indexOf("-");
        id = String.valueOf(item.substring(0, index));
        
        try {
            call = con.prepareCall("Exec sp_"+lista+"_delete("+id+")");
            call.executeUpdate();
            JOptionPane.showMessageDialog(this, "Eliminado con exito");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo eliminar\n");
        }
        
        try {      
            LlenarLista(lista);    
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Eliminar1ActionPerformed

    private void RolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_RolMouseClicked
        if(evt.isMetaDown()){
            Seguridad.show(evt.getComponent(),
                        evt.getX(), evt.getY());
            Roles.setVisible(false);
            Privilegios.setVisible(true);
        }
        setLastList("Rol");
    }//GEN-LAST:event_RolMouseClicked

    private void PrivilegioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PrivilegioMouseClicked
        if(evt.isMetaDown()){
            Seguridad.show(evt.getComponent(),
                        evt.getX(), evt.getY());
            Roles.setVisible(false);
            Privilegios.setVisible(false);
        }
        setLastList("Privilegio");
    }//GEN-LAST:event_PrivilegioMouseClicked

    private void VerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_VerActionPerformed
        String lista = "";
        if(getLastList().equalsIgnoreCase("usuario")){
            item = Usuario.getSelectedValue(); 
            lista = "usuario";
        }    
        else if (getLastList().equalsIgnoreCase("rol")){
            item = Rol.getSelectedValue();
            lista = "rol";
        }    
        else if (getLastList().equalsIgnoreCase("privilegio")){
            item = Privilegio.getSelectedValue();
            lista = "privilegio";
        }

        int index = item.indexOf("-");
        id = String.valueOf(item.substring(0, index));
        
        Ver newver;
        try {
            newver = new Ver (this, true, nombre, clave, getLastList(), id);
            newver.setVisible(true);
            this.setEnabled(false);
            LlenarLista(lista);
            this.setEnabled(true);
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_VerActionPerformed

    private void Crear_Nuevo_UsuarioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Crear_Nuevo_UsuarioMouseClicked
        id = IdUsuario.getText();
        flag = 0;
        permiso = false;
        
        for (int i = 0; i < roles.size(); i++) {
             if(roles.get(i).equalsIgnoreCase("admin"))
                 permiso = true;
        }
        
        try {
            query = "Select * from Usuario\n";
            rs = st.executeQuery(query);
            while (rs.next()){
                lastId = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if(!permiso)
           JOptionPane.showMessageDialog(this, "No tiene permiso para crear nuevos usuario");
        else{
            Registro newregistro = new Registro (this, true, id, lastId);
            newregistro.setVisible(true);
        }
        
        try {
            LlenarLista("Usuario");
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Crear_Nuevo_UsuarioMouseClicked

    private void RolesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RolesActionPerformed
        item = Usuario.getSelectedValue(); 
        int index = item.indexOf("-");
        String id_usuario_s = String.valueOf(item.substring(0, index));
        Roles rolesVer;
        rolesVer = new Roles (this, true, nombre, clave, id, id_usuario_s);
        rolesVer.setVisible(true);

    }//GEN-LAST:event_RolesActionPerformed

    private void PrivilegiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_PrivilegiosActionPerformed
        item = Rol.getSelectedValue(); 
        int index = item.indexOf("-");
        String id_rol = String.valueOf(item.substring(0, index));
        Privilegios PrivilegiosVer;
        PrivilegiosVer = new Privilegios (this, true, nombre, clave, id, id_rol);
        PrivilegiosVer.setVisible(true);
    }//GEN-LAST:event_PrivilegiosActionPerformed

    private void jTabbedPane2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTabbedPane2MouseClicked
        try {
            query = "Select r1.Id_Rol, r1.Nombre from Usuario_Rol u1\n" +
                    "inner join Rol r1 on u1.Id_Rol = r1.Id_Rol\n" +
                    "inner join Usuario u2 on u2.Id_Usuario = u1.Id_Usuario and u2.Id_Usuario =  "+id;
            rs = st.executeQuery(query);
            while (rs.next()){
                roles.add(rs.getString(2));
            }
            
            for(int i=0; i<roles.size(); i++){
                query = "Select p1.Id_Privilegio, p1.Nombre from Rol_Privilegio r1\n" +
                        "inner join Privilegio p1 on p1.Id_Privilegio = r1.Id_Privilegio\n" +
                        "inner join Rol r2 on r1.Id_Rol = r2.Id_Rol and r2.Nombre = '" +roles.get(i)+"'";
                rs = st.executeQuery(query);
                while (rs.next()){
                    privilegios.add(rs.getString(2));
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jTabbedPane2MouseClicked

    private void Crear_nuevo_rolMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Crear_nuevo_rolMouseClicked
        flag = 0;
        permiso = false;
        id = IdUsuario.getText();
        
        for (int i = 0; i < roles.size(); i++) {
             if(roles.get(i).equalsIgnoreCase("admin"))
                 permiso = true;
        }
        
        
        try {
            query = "Select * from Rol\n";
            rs = st.executeQuery(query);
            while (rs.next()){
                lastId = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if(!permiso)
           JOptionPane.showMessageDialog(this, "No tiene permiso para crear nuevos roles");
        else{
            NuevoRol newnuevorol = new NuevoRol (this, true, id, lastId);
            newnuevorol.setVisible(true);
        }
        
        try {
            LlenarLista("Rol");
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Crear_nuevo_rolMouseClicked

    private void Crear_nuevo_privilegioMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_Crear_nuevo_privilegioMouseClicked
        flag = 0;
        permiso = false;
        id = IdUsuario.getText();
        
        for (int i = 0; i < roles.size(); i++) {
             if(roles.get(i).equalsIgnoreCase("admin"))
                 permiso = true;
        }
        
        
        try {
            query = "Select * from Privilegio\n";
            rs = st.executeQuery(query);
            while (rs.next()){
                lastId = rs.getString(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        if(!permiso)
           JOptionPane.showMessageDialog(this, "No tiene permiso para crear nuevos privilegios");
        else{
            NuevoPrivilegio newpriv = new NuevoPrivilegio (this, true, id, lastId);
            newpriv.setVisible(true);
        }
        
        try {
            LlenarLista("Privilegio");
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_Crear_nuevo_privilegioMouseClicked

    private void jButton9MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton9MouseClicked
        Reporte newreporte;
        try {
            newreporte = new Reporte (this, true, nombre, clave, "Existencia de Productos");
            newreporte.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton9MouseClicked

    private void jButton10MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton10MouseClicked
        Reporte newreporte;
        try {
            newreporte = new Reporte (this, true, nombre, clave, "Existencia de Productos por debajo de la existencia mínima");
            newreporte.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton10MouseClicked

    private void jButton11MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton11MouseClicked
        Reporte newreporte;
        try {
            newreporte = new Reporte (this, true, nombre, clave, "Ventas por mes");
            newreporte.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton11MouseClicked

    private void jButton12MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton12MouseClicked
        Reporte newreporte;
        try {
            newreporte = new Reporte (this, true, nombre, clave, "Compras por producto");
            newreporte.setVisible(true);
        } catch (SQLException ex) {
            Logger.getLogger(Base.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton12MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Base.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Base().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Actualizar;
    private javax.swing.JMenuItem Agregar;
    private javax.swing.JPanel Ajuste;
    private javax.swing.JPanel Cliente;
    private javax.swing.JPanel Compra;
    private javax.swing.JPanel Crear_Nuevo_Usuario;
    private javax.swing.JPanel Crear_nuevo_privilegio;
    private javax.swing.JPanel Crear_nuevo_rol;
    private javax.swing.JPanel Detalle_Ajuste;
    private javax.swing.JPanel Detalle_Compra;
    private javax.swing.JPanel Detalle_Factura;
    private javax.swing.JMenuItem Eliminar;
    private javax.swing.JMenuItem Eliminar1;
    private javax.swing.JPanel Factura;
    private javax.swing.JLabel IdUsuario;
    private javax.swing.JPanel Kardex;
    private javax.swing.JLabel NombreUsuario;
    private javax.swing.JList<String> Privilegio;
    private javax.swing.JMenuItem Privilegios;
    private javax.swing.JPanel Producto;
    private javax.swing.JPanel Proveedor;
    private javax.swing.JList<String> Rol;
    private javax.swing.JMenuItem Roles;
    private javax.swing.JPopupMenu Seguridad;
    private javax.swing.JPopupMenu Tabla;
    private javax.swing.JList<String> Usuario;
    private javax.swing.JMenuItem Ver;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
    ResultSet rs; 
    Connection con;
    Statement st;
    String query;
    DatabaseMetaData dbmd;
    String nombre;
    String clave;
    DefaultTableModel model;
    DefaultListModel modelo;
    CallableStatement call;
    String id;
    String nombreLastPanel;
    String nombreLastList;
    String item;
    ArrayList<String> roles;
    ArrayList<String> privilegios;
    int flag;
    boolean permiso;
    String lastId;
}
