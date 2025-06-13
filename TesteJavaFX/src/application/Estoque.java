package application;

public class Estoque {
    private int id;
    private String nome;
    private String descricao;
    private double preco;
    
    public Estoque(int id, String nome, String descricao, double preco ) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        
    }

    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    

}
