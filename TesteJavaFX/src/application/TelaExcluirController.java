package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TelaExcluirController {

    @FXML
    private TableView<Produto> tabelaProdutos;

    @FXML
    private TableColumn<Produto, Integer> colunaId;

    @FXML
    private TableColumn<Produto, String> colunaNome;

    @FXML
    private TableColumn<Produto, Double> colunaPreco;

    @FXML
    private ImageView imagemSelecionada;

    @FXML
    public void initialize() {
        colunaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colunaNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colunaPreco.setCellValueFactory(new PropertyValueFactory<>("preco"));

        carregarProdutos();

        tabelaProdutos.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null && novo.getImagem() != null && !novo.getImagem().isEmpty()) {
                try {
                    File imgFile = new File(novo.getImagem());
                    if (imgFile.exists()) {
                        Image img = new Image(imgFile.toURI().toString());
                        imagemSelecionada.setImage(img);
                    } else {
                        imagemSelecionada.setImage(null);
                        System.out.println("Imagem não encontrada: " + imgFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    System.out.println("Erro ao carregar imagem: " + e.getMessage());
                    imagemSelecionada.setImage(null);
                }
            } else {
                imagemSelecionada.setImage(null);
            }
        });
    }

    private void carregarProdutos() {
        ObservableList<Produto> lista = FXCollections.observableArrayList();

        try (Connection conn = DBConnection.conectar()) {
            PreparedStatement stmt = conn.prepareStatement("SELECT id, nome, preco, imagem FROM produtos");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto p = new Produto(
                    rs.getInt("id"),
                    rs.getString("nome"),
                    rs.getDouble("preco"),
                    rs.getString("imagem")
                );
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tabelaProdutos.setItems(lista);
    }

    @FXML
    private void excluirProduto(ActionEvent event) {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();

        if (selecionado != null) {
            
            javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
            alerta.setTitle("Confirmação");
            alerta.setHeaderText(null);
            alerta.setContentText("Tem certeza que deseja excluir o produto \"" + selecionado.getNome() + "\"?");
            java.util.Optional<javafx.scene.control.ButtonType> resultado = alerta.showAndWait();

            if (resultado.isPresent() && resultado.get() == javafx.scene.control.ButtonType.OK) {
                try (Connection conn = DBConnection.conectar()) {
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM produtos WHERE id = ?");
                    stmt.setInt(1, selecionado.getId());
                    int linhas = stmt.executeUpdate();

                    if (linhas > 0) {
                        mostrarMensagem("Sucesso", "Produto excluído com sucesso!");
                        carregarProdutos();
                        imagemSelecionada.setImage(null);
                    }
                } catch (SQLException e) {
                    mostrarErro("Erro ao excluir", e.getMessage());
                }
            }
        } else {
            mostrarErro("Nenhum produto selecionado", "Selecione um item para excluir.");
        }
    }

    private void mostrarMensagem(String titulo, String mensagem) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    private void mostrarErro(String titulo, String mensagem) {
        javafx.scene.control.Alert alerta = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    
    
    
    
    
    
    
    
    
}
