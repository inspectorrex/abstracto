package dev.sheldan.abstracto.utility.models.database;

import dev.sheldan.abstracto.core.models.AUser;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name="starboard_post_reaction")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class StarboardPostReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reactorId")
    private AUser reactor;

    @ManyToOne
    @JoinColumn(name = "postId")
    private StarboardPost starboardPost;

}
