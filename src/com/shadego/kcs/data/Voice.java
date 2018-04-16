package com.shadego.kcs.data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Maclaine E-mail:deathencyclopedia@gmail.com
 * 
 */
public class Voice {

	private static final int[] voiceKey = { 604825, 607300, 613847, 615318, 624009, 631856, 635451, 637218, 640529, 643036, 652687, 658008, 662481, 669598, 675545, 685034, 687703,
	        696444, 702593, 703894, 711191, 714166, 720579, 728970, 738675, 740918, 743009, 747240, 750347, 759846, 764051, 770064, 773457, 779858, 786843, 790526, 799973, 803260,
	        808441, 816028, 825381, 827516, 832463, 837868, 843091, 852548, 858315, 867580, 875771, 879698, 882759, 885564, 888837, 896168 };

	public static Set<Integer> convertFileName(Integer id, Integer voiceType) {
		Set<Integer> set = new HashSet<>();
		int length = 0;
		switch (voiceType) {
		case 0:
			length = 28;
			break;
		case 1:
			length = 29;
			break;
		case 3:
			length = 53;
			break;
		case 7:
			length = 54;
			break;
		default:
			break;
		}
		for (int i = 1; i <= length; i++) {
			if (i == 6) {
				continue;
			} else if (i == 54) {
				set.add(129);
				continue;
			}
			set.add(convert(id, i));
		}
		return set;
	}

	public static Integer convert(Integer id, Integer voiceId) {
		return (id + 7) * 17 * (voiceKey[voiceId] - voiceKey[voiceId - 1]) % 99173 + 100000;
	}
}
