package cl.duoc.carreteras.dao;

import cl.duoc.carreteras.conexion.ConexionBD;
import cl.duoc.carreteras.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CarreteraDaoImpl implements CarreteraDAO {

    private static final String SQL_SELECT =
            "SELECT id, id_tipo, codigo, numero_pistas, longitud_km, "
                    + "tarifa_peaje, iluminada, tag_habilitado, altitud_maxima "
                    + "FROM carretera ORDER BY id";

    @Override
    public List<Carretera> obtenerTodos() throws PersistenciaException {
        List<Carretera> lista = new ArrayList<>();
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo completar la lectura de carreteras: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void insertar(Carretera carretera) throws PersistenciaException {

    }

    @Override
    public void actualizar(Carretera carretera) throws PersistenciaException {

    }

    @Override
    public void eliminar(int id) throws PersistenciaException {

    }

    private Carretera mapear(ResultSet rs) throws SQLException {
        Carretera c;
        if (rs.getInt("id_tipo") == 1) {
            CarreteraConcesionada con = new CarreteraConcesionada(
                    rs.getString("codigo"), rs.getInt("numero_pistas"),
                    rs.getDouble("longitud_km"), rs.getDouble("tarifa_peaje"),
                    rs.getInt("iluminada") == 1);
            con.setTagHabilitado(rs.getInt("tag_habilitado") == 1);
            c = con;
        } else {
            c = new CarreteraEstatal(
                    rs.getString("codigo"), rs.getInt("numero_pistas"),
                    rs.getDouble("longitud_km"), rs.getDouble("altitud_maxima"));
        }
        c.setId(rs.getInt("id"));
        return c;
    }

    // insertar, actualizar y eliminar: los vemos en los pasos 5 y 6
}
