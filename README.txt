Required java runtime environment 1.8 to run
directory structure:
. #current dir
	| data #copora dir
	|	| yahoonews-10fold-tagged #example corpus (CORPUS_DIR)
	|	|	| ...
	|	|	| inputs # created in processing
	|	|	| models # created in processing
	|	|	| outputs # created in processing
	|	|	| gold # user prepared
	|	|	| folds # user prepared
	
	| mallet-2.0.7 #mallet for crf
	| scripts #scripts dir
	|	| extract-features.sh #extract features from corpus, save to $CORPUS_DIR/features
	|	| format-crf-features.sh #format features to mallet crf format, save to $CORPUS_DIR/inputs
	|	| train-crf.sh # train crf, save models to $CORPUS_DIR/models, test outputs to $CORPUS_DIR/outputs
	|	| format-submission.sh # format to predefined
	|	| run-all.sh # run all above scripts
	| target # java build dir
