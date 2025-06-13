package application;

import javafx.stage.FileChooser;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class MainController {

    @FXML private TextField txtproduto;
    @FXML private TextField txtdescricao;
    @FXML private TextField txtpreco;
    @FXML private ImageView imagemProduto;
    @FXML private TextField caminhoImagem;

   
    @FXML private MenuButton btncategoria;
    @FXML private MenuItem itemLanche;
    @FXML private MenuItem itemBebida;

  
    private int categoriaSelecionada = -1;

    @FXML
    public void initialize() {
        if (itemLanche != null && itemBebida != null && btncategoria != null) {
            itemLanche.setOnAction(e -> {
                btncategoria.setText("Lanche");
                categoriaSelecionada = 1;
            });

            itemBebida.setOnAction(e -> {
                btncategoria.setText("Bebida");
                categoriaSelecionada = 2;
            });
        }
    }


    @FXML
    private void selecionarImagem(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecionar Imagem");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            String path = file.toURI().toString();
            imagemProduto.setImage(new Image(path));
            caminhoImagem.setText(file.getAbsolutePath());
        }
    }

    @FXML
    private void confirmarproduto(ActionEvent event) {
        String nomeProduto = txtproduto.getText();
        String descricaoProduto = txtdescricao.getText();
        String precoProduto = txtpreco.getText();

        if (nomeProduto == null || nomeProduto.trim().isEmpty()) {
            mostrarAlerta("Erro", "O nome do produto está vazio.");
            return;
        }

        if (precoProduto == null || precoProduto.trim().isEmpty()) {
            mostrarAlerta("Erro", "O preço do produto está vazio.");
            return;
        }

        if (categoriaSelecionada == -1) {
            mostrarAlerta("Erro", "Selecione uma categoria.");
            return;
        }

        try {
            double preco = Double.parseDouble(precoProduto);

            try (Connection conn = DBConnection.conectar()) {
                String sql = "INSERT INTO produtos (nome, descricao, preco, categoria_id, imagem) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nomeProduto);
                stmt.setString(2, descricaoProduto);
                stmt.setDouble(3, preco);
                stmt.setInt(4, categoriaSelecionada);
                stmt.setString(5, caminhoImagem.getText());

                int linhas = stmt.executeUpdate();

                if (linhas > 0) {
                    mostrarInfo("Sucesso", "Produto inserido com sucesso!");
                    limparCampos();
                } else {
                    mostrarAlerta("Erro", "Erro ao inserir produto.");
                }
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Preço inválido. Digite um número válido.");
        } catch (SQLException e) {
            mostrarAlerta("Erro no banco de dados", e.getMessage());
        }
    }

    @FXML
    private void abrirtelabebida(ActionEvent event) {
        abrirTela("/interfacegrafica/TelaBebida.fxml", "Cadastro De Bebida");
    }

    @FXML
    private void abriroutratela(ActionEvent event) {
        abrirTela("/interfacegrafica/TelaAlimento.fxml", "Cadastro de Alimento");
    }
    
    @FXML
    private void abrirtelaeditar(ActionEvent event) {
        abrirTela("/interfacegrafica/TelaEditar.fxml", "Cadastro de Alimento");
    }

    @FXML
    private void abrirtelaexcluir(ActionEvent event) {
        abrirTela("/interfacegrafica/TelaExcluir.fxml", "Excluir");
    }

    @FXML
    private void abrirtelaestoque(ActionEvent event) {
        abrirTela("/interfacegrafica/TelaEstoque.fxml", "Estoque");
    }

    private void abrirTela(String caminhoFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(caminhoFXML));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(titulo);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            System.out.println("Erro ao abrir a tela: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensagem) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void limparCampos() {
        txtproduto.clear();
        txtdescricao.clear();
        txtpreco.clear();
        imagemProduto.setImage(null);
        caminhoImagem.clear();
        btncategoria.setText("Categoria");
        categoriaSelecionada = -1;
    }
}
