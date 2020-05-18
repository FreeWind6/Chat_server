package mysql;

import javax.persistence.*;

@Entity
@Table(name = "blocklist")
public class Blocklist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "who")
    private Main who;

    @ManyToOne
    @JoinColumn(name = "whom")
    private Main whom;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Main getWho() {
        return who;
    }

    public void setWho(Main who) {
        this.who = who;
    }

    public Main getWhom() {
        return whom;
    }

    public void setWhom(Main whom) {
        this.whom = whom;
    }

    public Blocklist() {
    }

    public Blocklist(Main who, Main whom) {
        this.who = who;
        this.whom = whom;
    }
}
