package application;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TelaEdicaoProdutoController {

    @FXML private TextField campoId;
    @FXML private TextField campoNome;
    @FXML private TextField campoDescricao;
    @FXML private TextField campoPreco;
    @FXML private Button btnSalvar;
    @FXML private Button btnSelecionarImagem;
    @FXML private ImageView imagemView;

    private Produto produto;
    private String caminhoImagemEscolhida;

    public void setProduto(Produto produto) {
        this.produto = produto;
        preencherCampos();
    }

    private void preencherCampos() {
        campoId.setText(String.valueOf(produto.getId()));
        campoNome.setText(produto.getNome());
        campoDescricao.setText(produto.getDescricao());
        campoPreco.setText(String.valueOf(produto.getPreco()));
        caminhoImagemEscolhida = produto.getImagem();
        atualizarImagem(caminhoImagemEscolhida);
    }

    private void atualizarImagem(String caminhoImagem) {
        try {
            if (caminhoImagem != null && !caminhoImagem.isEmpty()) {
                String caminhoCorrigido = caminhoImagem.replace("\\", "/");

                if (!caminhoCorrigido.startsWith("file:/") && !caminhoCorrigido.startsWith("http")) {
                    if (!caminhoCorrigido.startsWith("/")) {
                        caminhoCorrigido = "/" + caminhoCorrigido;
                    }
                    caminhoCorrigido = "file:" + caminhoCorrigido;
                }

                Image imagem = new Image(caminhoCorrigido, true);
                imagemView.setImage(imagem);
            } else {
                imagemView.setImage(null);
            }
        } catch (Exception e) {
            System.out.println("Erro ao carregar imagem: " + e.getMessage());
            imagemView.setImage(null);
        }
    }

    @FXML
    public void initialize() {
        btnSelecionarImagem.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Selecionar Imagem do Produto");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imagens", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            Stage stage = (Stage) btnSelecionarImagem.getScene().getWindow();
            File arquivo = fileChooser.showOpenDialog(stage);

            if (arquivo != null) {
                caminhoImagemEscolhida = arquivo.getAbsolutePath();
                atualizarImagem(caminhoImagemEscolhida);
            }
        });

        btnSalvar.setOnAction(e -> {
            try {
              
                produto = new Produto(
                    produto.getId(),
                    campoNome.getText(),
                    campoDescricao.getText(),
                    Double.parseDouble(campoPreco.getText()),
                    caminhoImagemEscolhida
                );

                // Atualiza no BD
                boolean sucesso = atualizarProdutoNoBanco(produto);

                if (sucesso) {
                    System.out.println("Produto atualizado com sucesso!");
                    
                } else {
                    System.out.println("Falha ao atualizar o produto.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Preço inválido: " + ex.getMessage());
            }
        });
    }

    private boolean atualizarProdutoNoBanco(Produto produto) {
        String url = "jdbc:mysql://localhost:3306/bitesgo"; 
        String usuario = "root"; 
        String senha = "1234";   

        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, imagem = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setString(4, produto.getImagem());
            stmt.setInt(5, produto.getId());

            int linhasAfetadas = stmt.executeUpdate();
            return linhasAfetadas > 0;

        } catch (SQLException e) {
            System.out.println("Erro ao atualizar no banco: " + e.getMessage());
            return false;
        }
    }
}
