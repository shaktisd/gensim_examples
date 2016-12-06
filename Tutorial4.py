# -*- coding: utf-8 -*-
"""
Created on Tue Dec 06 05:16:34 2016

@author: Shakti
"""

from gensim import corpora, models, similarities
import os
from os import listdir
from os.path import isfile, join
from six import iteritems
from nltk.corpus import stopwords
from gensim.corpora import TextCorpus
from gensim.models.ldamodel import LdaModel

#Stopwords
stoplist = set(stopwords.words('english'))

dirname = 'docs'

#TODO
def tokenize(self):
    #return [word for word in self.document.split() if word not in stoplist]
    #self.document.spilt()
    return self.document.spilt()
              

onlyfiles = [join(dirname, f) for f in listdir(dirname) if isfile(join(dirname, f))]

# collect statistics about all tokens
class MyCorpus(TextCorpus): 
    def get_texts(self): 
        for filename in self.input: # for each relevant file 
            yield open(filename).read().split()


corpus = MyCorpus(onlyfiles) 
dictionary = corpus.dictionary

# remove stop words and words that appear only once
stop_ids = [dictionary.token2id[stopword] for stopword in stoplist
            if stopword in dictionary.token2id]
            
# remove stop words and words that appear only once
once_ids = [tokenid for tokenid, docfreq in iteritems(dictionary.dfs) if docfreq == 1]

# remove stop words and words that appear only once
dictionary.filter_tokens(stop_ids + once_ids)

# remove gaps in id sequence after words that were removed
dictionary.compactify()
print(dictionary.token2id)    

# store the dictionary, for future reference
dictionary.save('dictionary.dict')  
print('Saved dictionary')                
            
class MyFolderIterator(object):
    def __init__(self,dirname):
        self.dirname = dirname
    
    def __iter__(self):
        for filename in os.listdir(self.dirname):
            for line in open(os.path.join(self.dirname,filename)):
                yield dictionary.doc2bow(line.split())
                
corpus_memory_friendly = MyFolderIterator('docs')

corpora.MmCorpus.serialize('corpus.mm', corpus_memory_friendly)

print('Saved Corpus')

lda = LdaModel(corpus=corpus, id2word=dictionary, num_topics=5, update_every=1, passes=5)

print(lda[dictionary.doc2bow("research analyst report archive".split())])
