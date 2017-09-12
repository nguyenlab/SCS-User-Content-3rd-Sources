package lab.docsum.crf.features.thirdparty;

import java.util.ArrayList;
import java.util.List;
import edu.stanford.nlp.util.CoreMap;
import lab.docsum.crf.features.Feature;
import lab.docsum.misc.Doc;
import lab.docsum.misc.NLPUtils;
import lab.document.crf.features.rte.OtherFeature;

public class CosineVoting implements Feature<double[], List<Doc>> {

	double threshold = 0.65;
	
	public CosineVoting(double threshold){
		this.threshold = threshold;
	}
	
	@Override
	public double[] extract(List<Doc> input, Object... args) {
		// TODO Auto-generated method stub
		Doc doc = input.get(0);
		List<CoreMap> sents = doc.getSentences();
		double[] ft = new double[sents.size()];
		
		List<List<String>> lstSupportingSents = new ArrayList<>();
		for (int i = 1; i<input.size(); i++){
			for(CoreMap s : input.get(i).getSentences()){
				lstSupportingSents.add(NLPUtils.getTokenList.apply(s));
			}
		}
		for(int i = 0; i<sents.size(); i++){
			CoreMap annotation = sents.get(i);
			List<String> s = NLPUtils.getTokenList.apply(annotation);
			ft[i] = CosineVotingScore(s, lstSupportingSents);
		}
			
		return ft;
	}

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return "CosineVote";
	}
	
	double CosineVotingScore(List<String> s, List<List<String>> lst){
		int count = 0;
		if (lst.isEmpty()) return 0;
		for (List<String> h: lst){
			double cosine = OtherFeature.cosine(s, h);
			if (cosine>=threshold)
				count ++;
		}
		return (double) count/s.size();
	}
}
