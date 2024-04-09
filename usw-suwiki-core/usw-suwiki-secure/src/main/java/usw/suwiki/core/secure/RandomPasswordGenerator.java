package usw.suwiki.core.secure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomPasswordGenerator {
  private static final int RANDOM_CHARACTER_SIZE = 7;

  private static final SecureRandom RANDOM = new SecureRandom();

  private static final List<String> SYMBOLS = List.of("!", "@", "#", "$", "%", "^");
  private static final List<String> CHARACTERS = List.of(
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
    "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
    "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
    "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
    "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"
  );

  public static String generate() {
    var index = RANDOM.nextInt(RANDOM_CHARACTER_SIZE);
    var randomSymbol = SYMBOLS.get(RANDOM.nextInt(SYMBOLS.size()));

    var randomCharacters = RANDOM.ints(RANDOM_CHARACTER_SIZE, 0, CHARACTERS.size())
      .mapToObj(CHARACTERS::get)
      .collect(Collectors.joining());

    return randomCharacters.substring(0, index) + randomSymbol + randomCharacters.substring(index);
  }
}
