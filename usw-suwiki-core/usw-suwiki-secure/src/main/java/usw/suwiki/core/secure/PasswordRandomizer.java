package usw.suwiki.core.secure;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PasswordRandomizer {

  public static String randomizePassword() {
    SecureRandom secureRandom = new SecureRandom();
    secureRandom.setSeed(new Date().getTime());

    char[] charAllSet = new char[]{
      '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
      'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
      'R', 'S',
      'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
      'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
      'r', 's',
      't', 'u', 'v', 'w', 'x', 'y', 'z',
      '!', '@', '#', '$', '%', '^'
    };
    char[] charNumberSet = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    char[] charSpecialSet = new char[]{'!', '@', '#', '$', '%', '^'};
    int idx = 0;
    int allLen = charAllSet.length;
    int numberLen = charNumberSet.length;
    int specialLen = charSpecialSet.length;

    StringBuilder newPassword = new StringBuilder();
    for (int i = 0; i < 1; i++) {
      idx = secureRandom.nextInt(numberLen);
      newPassword.append(charNumberSet[idx]);
    }

    for (int i = 0; i < 1; i++) {
      idx = secureRandom.nextInt(specialLen);
      newPassword.append(charSpecialSet[idx]);
    }

    for (int i = 0; i < 6; i++) {
      idx = secureRandom.nextInt(allLen);
      newPassword.append(charAllSet[idx]);
    }
    return newPassword.toString();
  }

}
