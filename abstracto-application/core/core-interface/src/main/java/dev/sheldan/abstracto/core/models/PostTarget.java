package dev.sheldan.abstracto.core.models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name="posttarget")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    private Long id;

    @Column(unique = true)
    @Getter
    private String name;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "channel_id", nullable = false)
    @Getter @Setter
    private AChannel channelReference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="server_id", nullable = false)
    @Getter @Setter
    private AServer serverReference;

    public static String JOIN_LOG = "joinlog";
    public static String LEAVE_LOG = "leavelog";
    public static String WARN_LOG = "warnlog";
    public static String KICK_LOG = "kicklog";
    public static String BAN_LOG = "banlog";

    public static List<String> AVAILABLE_POST_TARGETS = Arrays.asList(JOIN_LOG, LEAVE_LOG, WARN_LOG, KICK_LOG, BAN_LOG);
}
