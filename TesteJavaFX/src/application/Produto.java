package application;

public class Produto {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    private String imagem;

    public Produto(int id, String nome, String descricao, double preco, String imagem) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.imagem = imagem;
    }

   
    public Produto(int id, String nome, double preco, String imagem) {
        this(id, nome, "", preco, imagem);
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public String getImagem() { return imagem; }

    
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setImagem(String imagem) { this.imagem = imagem; }
}
