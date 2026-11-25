package cl.duoc.carreteras.dao;

import cl.duoc.carreteras.conexion.ConexionBD;
import cl.duoc.carreteras.model.Carretera;
import cl.duoc.carreteras.model.CarreteraConcesionada;
import cl.duoc.carreteras.model.CarreteraEstatal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CarreteraDaoImpl implements CarreteraDAO {

    private static final String SQL_SELECT =
            "SELECT id, id_tipo, codigo, numero_pistas, longitud_km, "
                    + "tarifa_peaje, iluminada, tag_habilitado, altitud_maxima "
                    + "FROM carretera ORDER BY id";

    private static final String SQL_INSERT =
            "INSERT INTO carretera (id_tipo, codigo, numero_pistas, longitud_km, "
                    + "tarifa_peaje, iluminada, tag_habilitado, altitud_maxima) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE carretera SET id_tipo = ?, codigo = ?, numero_pistas = ?, "
                    + "longitud_km = ?, tarifa_peaje = ?, iluminada = ?, tag_habilitado = ?, "
                    + "altitud_maxima = ? WHERE id = ?";

    private static final String SQL_DELETE = "DELETE FROM carretera WHERE id = ?";

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
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT)) {
            asignarParametros(ps, carretera);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo completar el registro de la carretera: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Carretera carretera) throws PersistenciaException {
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {
            asignarParametros(ps, carretera);
            ps.setInt(9, carretera.getId());
            if (ps.executeUpdate() == 0) {
                throw new PersistenciaException(
                        "No se pudo completar la actualización: no existe la carretera con id "
                                + carretera.getId() + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo completar la actualización de la carretera: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) throws PersistenciaException {
        try (Connection con = ConexionBD.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new PersistenciaException(
                        "No se pudo completar la eliminación: no existe la carretera con id "
                                + id + ".");
            }
        } catch (SQLException e) {
            throw new PersistenciaException(
                    "No se pudo completar la eliminación de la carretera: " + e.getMessage(), e);
        }
    }

    private void asignarParametros(PreparedStatement ps, Carretera c) throws SQLException {
        ps.setString(2, c.getCodigo());
        ps.setInt(3, c.getNumeroPistas());
        ps.setDouble(4, c.getLongitudKm());

        if (c instanceof CarreteraConcesionada con) {
            ps.setInt(1, 1);
            ps.setDouble(5, con.getTarifaPeaje());
            ps.setInt(6, con.isIluminada() ? 1 : 0);
            ps.setInt(7, con.isTagHabilitado() ? 1 : 0);
            ps.setNull(8, Types.DECIMAL);
        } else if (c instanceof CarreteraEstatal est) {
            ps.setInt(1, 2);
            ps.setNull(5, Types.DECIMAL);
            ps.setNull(6, Types.INTEGER);
            ps.setNull(7, Types.INTEGER);
            ps.setDouble(8, est.getAltitudMaxima());
        }
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
}