package io.github.xeelsx.pixelmonteamlogger;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.storage.PlayerPartyStorage;
import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;

import java.sql.*;
import java.util.Arrays;
import java.util.Optional;

import static java.sql.DriverManager.getConnection;

public class LogTeam implements CommandExecutor {
    private final int PARTY_SIZE = 6;
    private final int TOTAL_COLUMNS = 13; //When changing this make sure that any sql statements, corresponding ifs & for loops are adjusted correctly.
    //Is there a dynamic way to count columns & columns in ResultSet in java???
    private final int MOVE1_ID = 0;
    private final int MOVE2_ID = 1;
    private final int MOVE3_ID = 2;
    private final int MOVE4_ID = 3;

    private Boolean PlayerLogged(Connection conn, EntityPlayerMP player) throws SQLException {
        String sql = "SELECT DISTINCT User FROM pixelmonteamlog WHERE User = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, player.getUniqueID().toString());
        ResultSet results = stmt.executeQuery();
        return results.first();
    }

    private void TeamLog(Connection conn, CommandSource src, EntityPlayerMP player, PlayerPartyStorage party) throws SQLException {
        String sql;
        PreparedStatement stmt;
        src.sendMessage(Text.builder(player.getName() + "'s team has been logged: (Hover for more information)").color(TextColors.LIGHT_PURPLE).build());
        for (int i = 0; i < PARTY_SIZE; i++) {
            Pokemon pokemon = party.get(i);
            if (pokemon == null) {
                sql = String.format("INSERT INTO pixelmonteamlog (User, Slot) VALUES (?, ?)",
                        player.getUniqueID(),
                        i);
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, player.getUniqueID().toString());
                stmt.setInt(2, i);
            } else {
                sql = String.format("INSERT INTO pixelmonteamlog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, player.getUniqueID().toString());
                stmt.setInt(2, i);
                stmt.setString(3, pokemon.getDisplayName());
                stmt.setInt(4, pokemon.getLevel());
                stmt.setString(5, pokemon.getAbilityName());
                stmt.setString(6, pokemon.getNature().getName());
                stmt.setString(7, pokemon.getHeldItem().getDisplayName());
                stmt.setString(8, String.format("%s/%s/%s/%s/%s/%s",
                        pokemon.getIVs().hp,
                        pokemon.getIVs().attack,
                        pokemon.getIVs().defence,
                        pokemon.getIVs().specialAttack,
                        pokemon.getIVs().specialDefence,
                        pokemon.getIVs().speed));
                stmt.setString(9, String.format("%s/%s/%s/%s/%s/%s",
                        pokemon.getEVs().hp,
                        pokemon.getEVs().attack,
                        pokemon.getEVs().defence,
                        pokemon.getEVs().specialAttack,
                        pokemon.getEVs().specialDefence,
                        pokemon.getEVs().speed));
                stmt.setString(10, String.format("%s", pokemon.getMoveset().get(MOVE1_ID)));
                stmt.setString(11, String.format("%s", pokemon.getMoveset().get(MOVE2_ID)));
                stmt.setString(12, String.format("%s", pokemon.getMoveset().get(MOVE3_ID)));
                stmt.setString(13, String.format("%s", pokemon.getMoveset().get(MOVE4_ID)));
            }
            stmt.executeQuery();
            if (pokemon == null) {
                src.sendMessage(Text.builder("Empty").color(TextColors.YELLOW).build());
            } else {
                src.sendMessage(Text.of(
                        Text.builder(pokemon.getDisplayName()).color(TextColors.YELLOW).build(), " ",
                        Text.builder("[Stats]").color(TextColors.RED).onHover(TextActions.showText(Text.of(
                                Text.builder("Level: " + pokemon.getLevel()).color(TextColors.RED).build(),
                                Text.NEW_LINE,
                                Text.builder("Ability: " + pokemon.getAbilityName()).color(TextColors.GOLD).build(),
                                Text.NEW_LINE,
                                Text.builder("Nature: " + pokemon.getNature().getName()).color(TextColors.YELLOW).build(),
                                Text.NEW_LINE,
                                Text.builder("Item: " + pokemon.getHeldItem().getDisplayName()).color(TextColors.GREEN).build()
                        ))).build(), " ",
                        Text.builder("[IVs]").color(TextColors.GREEN).onHover(TextActions.showText(Text.of(
                                Text.builder("HP: " + pokemon.getIVs().hp).color(TextColors.RED).build(),
                                Text.NEW_LINE,
                                Text.builder("Attack: " + pokemon.getIVs().attack).color(TextColors.GOLD).build(),
                                Text.NEW_LINE,
                                Text.builder("Defense: " + pokemon.getIVs().defence).color(TextColors.YELLOW).build(),
                                Text.NEW_LINE,
                                Text.builder("Special Attack: " + pokemon.getIVs().specialAttack).color(TextColors.GREEN).build(),
                                Text.NEW_LINE,
                                Text.builder("Special Defense: " + pokemon.getIVs().specialDefence).color(TextColors.AQUA).build(),
                                Text.NEW_LINE,
                                Text.builder("Speed: " + pokemon.getIVs().speed).color(TextColors.LIGHT_PURPLE).build()
                        ))).build(), " ",
                        Text.builder("[EVs]").color(TextColors.AQUA).onHover(TextActions.showText(Text.of(
                                Text.builder("HP: " + pokemon.getEVs().hp).color(TextColors.RED).build(),
                                Text.NEW_LINE,
                                Text.builder("Attack: " + pokemon.getEVs().attack).color(TextColors.GOLD).build(),
                                Text.NEW_LINE,
                                Text.builder("Defense: " + pokemon.getEVs().defence).color(TextColors.YELLOW).build(),
                                Text.NEW_LINE,
                                Text.builder("Special Attack: " + pokemon.getEVs().specialAttack).color(TextColors.GREEN).build(),
                                Text.NEW_LINE,
                                Text.builder("Special Defense: " + pokemon.getEVs().specialDefence).color(TextColors.AQUA).build(),
                                Text.NEW_LINE,
                                Text.builder("Speed: " + pokemon.getEVs().speed).color(TextColors.LIGHT_PURPLE).build()
                        ))).build(), " ",
                        Text.builder("[Moves]").color(TextColors.LIGHT_PURPLE).onHover(TextActions.showText(Text.of(
                                Text.builder("Move1: " + pokemon.getMoveset().get(MOVE1_ID)).color(TextColors.RED).build(),
                                Text.NEW_LINE,
                                Text.builder("Move2: " + pokemon.getMoveset().get(MOVE2_ID)).color(TextColors.GOLD).build(),
                                Text.NEW_LINE,
                                Text.builder("Move3: " + pokemon.getMoveset().get(MOVE3_ID)).color(TextColors.YELLOW).build(),
                                Text.NEW_LINE,
                                Text.builder("Move4: " + pokemon.getMoveset().get(MOVE4_ID)).color(TextColors.GREEN).build()
                        ))).build()
                ));
            }
        }
    }

    private DisplayPokemonContainer[] GetLoggedTeam(Connection conn, EntityPlayerMP player, CommandSource src) throws SQLException {
        src.sendMessage(Text.of("FirstTest"));
        String sql = "SELECT * FROM pixelmonteamlog WHERE User = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, player.getUniqueID().toString());
        ResultSet results = stmt.executeQuery();
        DisplayPokemonContainer[] pokemonArr = new DisplayPokemonContainer[PARTY_SIZE];
        for (int i = 0; results.next(); i++) {
            pokemonArr[i].displayName = results.getString("Pokemon");
            pokemonArr[i].level = results.getInt("Level");
            pokemonArr[i].ability = results.getString("Ability");
            pokemonArr[i].nature = results.getString("Nature");
            pokemonArr[i].item = results.getString("Item");
            pokemonArr[i].ivs = results.getString("IVs");
            pokemonArr[i].evs = results.getString("EVs");
            pokemonArr[i].move1 = results.getString("Move1");
            pokemonArr[i].move2 = results.getString("Move2");
            pokemonArr[i].move3 = results.getString("Move3");
            pokemonArr[i].move4 = results.getString("Move4");
            src.sendMessage(Text.of("This worked"));
        }
        return pokemonArr;
    }
    private boolean CompareTeam(Connection conn, EntityPlayerMP player, PlayerPartyStorage party) throws SQLException {
        String sql = "SELECT User, Slot, Pokemon, Level, Ability, Nature, Item, IVs, EVs, Move1, Move2, Move3, Move4 FROM pixelmonteamlog WHERE User = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, player.getUniqueID().toString());
        ResultSet results = stmt.executeQuery();

        boolean[] match = new boolean[PARTY_SIZE];

        while(results.next()) {
            for (int i = 0; i < PARTY_SIZE; i++) {
                Pokemon pokemon = party.get(i);
                if (pokemon == null && !match[i]) {
                    boolean allNull = true;
                    for (int j = 2; j < TOTAL_COLUMNS; j++) {
                        String init = results.getString("Pokemon");
                        if (!(results.wasNull())) {
                            allNull = false;
                            break;
                        }
                    }
                    if (allNull) {
                        match[i] = true;
                        break;
                    }
                } else {
                    //Compare data in sql to slot data
                    //If does then set match (corresponding slot) to true
                    //If no matches then does nothing

                    //On if statement is it possible to get name of moves, till then just using == rather than equals
                    if (!match[i]
                            && pokemon.getDisplayName().toString().equals(results.getString(3))) {
                        match[i] = true;
                        break;
                    }
                }
            }
        }
        //check all of match is true
        for (int i = 0; i < PARTY_SIZE; i++) {
            if (!match[i]) {
                return false;   //Teams don't match
            }
        }
        return true;    //Teams match
    }

    private void DeleteTeam(Connection conn, EntityPlayerMP player, CommandSource src) throws SQLException {
        String sql = "DELETE FROM pixelmonteamlog WHERE User = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, player.getUniqueID().toString());
        stmt.executeQuery();
        src.sendMessage(Text.builder(player.getName() + "'s team has been deleted.").color(TextColors.GREEN).build());
    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof EntityPlayerMP) {
            LogTeamArgument args1 = args.<LogTeamArgument>getOne("argument").get();
            EntityPlayerMP player = args.<EntityPlayerMP>getOne("player").get();
            PlayerPartyStorage party = Pixelmon.storageManager.getParty(player);

            try
            {
                String url = "jdbc:mysql://localhost:3306/plgn";
                Connection conn = getConnection(url, "root", ""); //Creates connection to database

                if (args1 == LogTeamArgument.log) {
                    if (PlayerLogged(conn, player) == true) {
                        src.sendMessage(Text.builder("Team already exists!").color(TextColors.RED).build());
                        src.sendMessage(Text.builder("To log a new team for a player: /logteam overide <player>").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                    TeamLog(conn, src, player, party);
                } else if (args1 == LogTeamArgument.compare) {
                    if (PlayerLogged(conn, player) == false) {
                        src.sendMessage(Text.builder("No team found!").color(TextColors.RED).build());
                        src.sendMessage(Text.builder("To log a team: /logteam log <player>").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                    boolean match = CompareTeam(conn, player, party);
                    if (match) {
                        src.sendMessage(Text.of("Teams match!"));
                    } else {
                        src.sendMessage(Text.of("Team do not match!"));
                    }
                } else if (args1 == LogTeamArgument.overide) {
                    if (PlayerLogged(conn, player) == false) {
                        src.sendMessage(Text.builder("No team found!").color(TextColors.RED).build());
                        src.sendMessage(Text.builder("To log a team: /logteam log <player>").color(TextColors.RED).build());
                        return CommandResult.empty();
                    }
                    DeleteTeam(conn, player, src);
                    TeamLog(conn, src, player, party);
                } else if (args1 == LogTeamArgument.delete) {
                    //METHOD TO CONFIRM TEAM
                    DeleteTeam(conn, player, src);
                } else {
                    return CommandResult.empty();
                }
            } catch (SQLException e) {
                src.sendMessage(Text.of(e));
                return CommandResult.empty();
            }
            return CommandResult.success();
        }

        src.sendMessage(Text.of("This command must be run by an instance of player."));
        return CommandResult.success();
    }
}
