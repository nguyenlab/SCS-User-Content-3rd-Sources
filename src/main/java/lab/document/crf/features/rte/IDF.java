package lab.document.crf.features.rte;

import java.util.List;

public class IDF {

	// Total common words of 2 sentence
	public static double common(List<String> t, List<String> h) {
		double comm = 0;
		for (int i = 0; i < h.size(); i++) {
			for (int j = 0; j < t.size(); j++) {
				if (h.get(i).equals(t.get(j))) {
					comm++;
					break;
				}
			}
		}
		return comm;
	}

	// Tinh tan suat xuat hien cua cac tu t c� trong h
	public static double occurH(List<String> t, List<String> h) {
		double comm = common(t, h);
		return comm / (double) h.size();

	}

	// Tinh tan suat xuat hien cua cac tu h c� trong t
	public static double occurT(List<String> t, List<String> h) {
		double comm = common(t, h);
		return comm / (double) t.size();

	}

	// Tan suat xuat hien cua 1 tu trong cau khc
	static double[] frequecy(List<String> a, List<String> b) {
		double fre[] = new double[b.size()];
		for (int i = 0; i < b.size(); i++) {
			double tmp = 0;
			for (int j = 0; j < a.size(); j++) {
				if (b.get(i).equals(a.get(j)))
					tmp++;
			}
			fre[(i)] = tmp;
		}
		return fre;
	}

/*	public static static void main(List<String> args) {
		List<String> a = { "a", "a", "b", "d", "e", "e", "a", "f", "l", "g" };
		List<String> b = { "a", "a", "b", "d", "e", "f", "l" };
		double[] fre = new double[b.size()];
		double comm = common(a, b);
		double comm1 = occurH(a, b);
		double comm2 = occurT(a, b);
		fre = frequecy(a, b);
		for (int i = 0; i < fre.size(); i++)
			System.out.print(fre.get(i) + "  ");

		System.out.print(comm + "  " + comm1 + " " + comm2);

	} */

}
