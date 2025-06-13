package application;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaEditarController {

    @FXML
    private TableView<Produto> tabelaEditar;

    @FXML
    private TableColumn<Produto, Integer> colunaId;

    @FXML
    private TableColumn<Produto, String> colunaNome;

    @FXML
    public void initialize() {
        tabelaEditar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                Produto produtoSelecionado = tabelaEditar.getSelectionModel().getSelectedItem();
                if (produtoSelecionado != null) {
                    abrirTelaDeEdicao(produtoSelecionado);
                }
            }
        });

        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        carregarProdutos();
    }

    private void carregarProdutos() {
        ObservableList<Produto> lista = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.conectar()) {
        	PreparedStatement stmt = conn.prepareStatement("SELECT id, nome, descricao, preco, imagem FROM produtos");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto p = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getString("descricao"),
                    rs.getDouble("preco"),
                    rs.getString("imagem")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tabelaEditar.setItems(lista);
    }

    private void abrirTelaDeEdicao(Produto produto) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfacegrafica/TelaEdicaoProduto.fxml"));
            Parent root = loader.load();

            TelaEdicaoProdutoController controller = loader.getController();
            controller.setProduto(produto); // passa o produto para a nova tela

            Stage stage = new Stage();
            stage.setTitle("Editar Produto");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
