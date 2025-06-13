package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaEstoqueController {

    @FXML
    private TableView<Estoque> tabelaEstoque;

    @FXML
    private TableColumn<Estoque, Integer> colunaId;

    @FXML
    private TableColumn<Estoque, String> colunaNome;

    @FXML
    private TableColumn<Estoque, String> colunaDescricao;

    @FXML
    private TableColumn<Estoque, Double> colunaPreco;

    @FXML
    private TextField campoPesquisa;

    @FXML
    private MenuButton btnCategoriaEstoque;

    @FXML
    private MenuItem itemLancheEstoque;

    @FXML
    private MenuItem itemBebidaEstoque;

    @FXML 
    private MenuItem itemTodosEstoque;
    
    private int categoriaFiltrada = -1;

    @FXML
    public void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        itemTodosEstoque.setOnAction(e -> {
            categoriaFiltrada = -1;
            btnCategoriaEstoque.setText("Todos");
            carregarProdutos();
        });

        itemLancheEstoque.setOnAction(e -> {
            categoriaFiltrada = 1;
            btnCategoriaEstoque.setText("Lanche");
            carregarProdutos();
        });

        itemBebidaEstoque.setOnAction(e -> {
            categoriaFiltrada = 2;
            btnCategoriaEstoque.setText("Bebida");
            carregarProdutos();
        });

        campoPesquisa.textProperty().addListener((obs, oldVal, newVal) -> carregarProdutos());

        carregarProdutos();
    }


    private void carregarProdutos() {
        ObservableList<Estoque> lista = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.conectar()) {
            StringBuilder sql = new StringBuilder("SELECT id, nome, descricao, preco FROM produtos WHERE 1=1");

            if (categoriaFiltrada != -1) {
                sql.append(" AND categoria_id = ").append(categoriaFiltrada);
            }

            String texto = campoPesquisa.getText();
            boolean temTexto = texto != null && !texto.trim().isEmpty();

            if (temTexto) {
                sql.append(" AND nome LIKE ?");
            }

            PreparedStatement stmt = conn.prepareStatement(sql.toString());

            if (temTexto) {
                stmt.setString(1, "%" + texto.trim() + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Estoque produto = new Estoque(
                        rs.getInt("id"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getDouble("preco")
                );
                lista.add(produto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tabelaEstoque.setItems(lista);
    }
}
