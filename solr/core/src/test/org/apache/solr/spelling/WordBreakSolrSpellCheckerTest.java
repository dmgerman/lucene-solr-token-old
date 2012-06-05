begin_unit
begin_package
DECL|package|org.apache.solr.spelling
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|spelling
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|MockAnalyzer
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Token
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|SolrTestCaseJ4
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|core
operator|.
name|SolrCore
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|handler
operator|.
name|component
operator|.
name|SpellCheckComponent
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|search
operator|.
name|SolrIndexSearcher
import|;
end_import
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|util
operator|.
name|RefCounted
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import
begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import
begin_class
DECL|class|WordBreakSolrSpellCheckerTest
specifier|public
class|class
name|WordBreakSolrSpellCheckerTest
extends|extends
name|SolrTestCaseJ4
block|{
annotation|@
name|BeforeClass
DECL|method|beforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|initCore
argument_list|(
literal|"solrconfig-spellcheckcomponent.xml"
argument_list|,
literal|"schema.xml"
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"0"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"pain table paintablepine pi ne in able"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"1"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"paint able pineapple goodness in"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"2"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"pa in table pineapplegoodness"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"3"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"printable line in ample food mess"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"4"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"printable in pointable paint able"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"5"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"printable in puntable paint able "
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|adoc
argument_list|(
literal|"id"
argument_list|,
literal|"6"
argument_list|,
literal|"lowerfilt"
argument_list|,
literal|"paint able in pintable plantable"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|h
operator|.
name|validateUpdate
argument_list|(
name|commit
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|//docfreq=7:  in
comment|//docfreq=5:  able
comment|//docfreq=4:  paint
comment|//docfreq=3:  printable
comment|//docfreq=2:  table
comment|//docfreq=1:  {all others}
block|}
annotation|@
name|Test
DECL|method|testStandAlone
specifier|public
name|void
name|testStandAlone
parameter_list|()
throws|throws
name|Exception
block|{
name|SolrCore
name|core
init|=
name|h
operator|.
name|getCore
argument_list|()
decl_stmt|;
name|WordBreakSolrSpellChecker
name|checker
init|=
operator|new
name|WordBreakSolrSpellChecker
argument_list|()
decl_stmt|;
name|NamedList
argument_list|<
name|String
argument_list|>
name|params
init|=
operator|new
name|NamedList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"field"
argument_list|,
literal|"lowerfilt"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|WordBreakSolrSpellChecker
operator|.
name|PARAM_BREAK_WORDS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|WordBreakSolrSpellChecker
operator|.
name|PARAM_COMBINE_WORDS
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|WordBreakSolrSpellChecker
operator|.
name|PARAM_MAX_CHANGES
argument_list|,
literal|"10"
argument_list|)
expr_stmt|;
name|checker
operator|.
name|init
argument_list|(
name|params
argument_list|,
name|core
argument_list|)
expr_stmt|;
name|RefCounted
argument_list|<
name|SolrIndexSearcher
argument_list|>
name|searcher
init|=
name|core
operator|.
name|getSearcher
argument_list|()
decl_stmt|;
name|QueryConverter
name|qc
init|=
operator|new
name|SpellingQueryConverter
argument_list|()
decl_stmt|;
name|qc
operator|.
name|setAnalyzer
argument_list|(
operator|new
name|MockAnalyzer
argument_list|(
name|random
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Collection
argument_list|<
name|Token
argument_list|>
name|tokens
init|=
name|qc
operator|.
name|convert
argument_list|(
literal|"paintable pine apple good ness"
argument_list|)
decl_stmt|;
name|SpellingOptions
name|spellOpts
init|=
operator|new
name|SpellingOptions
argument_list|(
name|tokens
argument_list|,
name|searcher
operator|.
name|get
argument_list|()
operator|.
name|getIndexReader
argument_list|()
argument_list|,
literal|10
argument_list|)
decl_stmt|;
name|SpellingResult
name|result
init|=
name|checker
operator|.
name|getSuggestions
argument_list|(
name|spellOpts
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|decref
argument_list|()
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|!=
literal|null
operator|&&
name|result
operator|.
name|getSuggestions
argument_list|()
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|result
operator|.
name|getSuggestions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|6
argument_list|)
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Token
argument_list|,
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
argument_list|>
name|s
range|:
name|result
operator|.
name|getSuggestions
argument_list|()
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|Token
name|orig
init|=
name|s
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
index|[]
name|corr
init|=
name|s
operator|.
name|getValue
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"paintable"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|9
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|9
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|3
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"paint able"
argument_list|)
argument_list|)
expr_stmt|;
comment|//1 op ; max doc freq=5
name|assertTrue
argument_list|(
name|corr
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"pain table"
argument_list|)
argument_list|)
expr_stmt|;
comment|//1 op ; max doc freq=2
name|assertTrue
argument_list|(
name|corr
index|[
literal|2
index|]
operator|.
name|equals
argument_list|(
literal|"pa in table"
argument_list|)
argument_list|)
expr_stmt|;
comment|//2 ops
block|}
elseif|else
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"pine apple"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|20
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"pineapple"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"paintable pine"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|0
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|14
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|14
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"paintablepine"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"good ness"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|21
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|30
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|9
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"goodness"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"pine apple good ness"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|30
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|20
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"pineapplegoodness"
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|orig
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
literal|"pine"
argument_list|)
condition|)
block|{
name|assertTrue
argument_list|(
name|orig
operator|.
name|startOffset
argument_list|()
operator|==
literal|10
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|endOffset
argument_list|()
operator|==
literal|14
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|orig
operator|.
name|length
argument_list|()
operator|==
literal|4
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
operator|.
name|length
operator|==
literal|1
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|corr
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"pi ne"
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Unexpected original result: "
operator|+
name|orig
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
DECL|method|testInConjunction
specifier|public
name|void
name|testInConjunction
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(paintable pine apple good ness)"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|)
argument_list|,
literal|"//lst[@name='suggestions']/lst[1]/@name='paintable'"
argument_list|,
literal|"//lst[@name='suggestions']/lst[2]/@name='pine'"
argument_list|,
literal|"//lst[@name='suggestions']/lst[3]/@name='apple'"
argument_list|,
literal|"//lst[@name='suggestions']/lst[4]/@name='good'"
argument_list|,
literal|"//lst[@name='suggestions']/lst[5]/@name='ness'"
argument_list|,
literal|"//lst[@name='paintable']/int[@name='numFound']=8"
argument_list|,
literal|"//lst[@name='paintable']/int[@name='startOffset']=11"
argument_list|,
literal|"//lst[@name='paintable']/int[@name='endOffset']=20"
argument_list|,
literal|"//lst[@name='paintable']/int[@name='origFreq']=0"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[1]/str[@name='word']='printable'"
argument_list|,
comment|//SolrSpellChecker result interleaved
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[1]/int[@name='freq']=3"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[2]/str[@name='word']='paint able'"
argument_list|,
comment|//1 op
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[2]/int[@name='freq']=5"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[3]/str[@name='word']='pintable'"
argument_list|,
comment|//SolrSpellChecker result interleaved
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[3]/int[@name='freq']=1"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[4]/str[@name='word']='pain table'"
argument_list|,
comment|//1 op
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[4]/int[@name='freq']=2"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[5]/str[@name='word']='pointable'"
argument_list|,
comment|//SolrSpellChecker result interleaved
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[5]/int[@name='freq']=1"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[6]/str[@name='word']='pa in table'"
argument_list|,
comment|//2 ops
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[6]/int[@name='freq']=7"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[7]/str[@name='word']='plantable'"
argument_list|,
comment|//SolrSpellChecker result interleaved
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[7]/int[@name='freq']=1"
argument_list|,
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[8]/str[@name='word']='puntable'"
argument_list|,
comment|//SolrSpellChecker result interleaved
literal|"//lst[@name='paintable']/arr[@name='suggestion']/lst[8]/int[@name='freq']=1"
argument_list|,
literal|"//lst[@name='pine']/int[@name='numFound']=2"
argument_list|,
literal|"//lst[@name='pine']/int[@name='startOffset']=21"
argument_list|,
literal|"//lst[@name='pine']/int[@name='endOffset']=25"
argument_list|,
literal|"//lst[@name='pine']/arr[@name='suggestion']/lst[1]/str[@name='word']='line'"
argument_list|,
literal|"//lst[@name='pine']/arr[@name='suggestion']/lst[2]/str[@name='word']='pi ne'"
argument_list|,
literal|"//lst[@name='apple']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='apple']/arr[@name='suggestion']/lst[1]/str[@name='word']='ample'"
argument_list|,
literal|"//lst[@name='good']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='good']/arr[@name='suggestion']/lst[1]/str[@name='word']='food'"
argument_list|,
literal|"//lst[@name='ness']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='ness']/arr[@name='suggestion']/lst[1]/str[@name='word']='mess'"
argument_list|,
literal|"//lst[@name='pine apple']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='pine apple']/int[@name='startOffset']=21"
argument_list|,
literal|"//lst[@name='pine apple']/int[@name='endOffset']=31"
argument_list|,
literal|"//lst[@name='pine apple']/arr[@name='suggestion']/lst[1]/str[@name='word']='pineapple'"
argument_list|,
literal|"//lst[@name='paintable pine']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='paintable pine']/int[@name='startOffset']=11"
argument_list|,
literal|"//lst[@name='paintable pine']/int[@name='endOffset']=25"
argument_list|,
literal|"//lst[@name='paintable pine']/arr[@name='suggestion']/lst[1]/str[@name='word']='paintablepine'"
argument_list|,
literal|"//lst[@name='good ness']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='good ness']/int[@name='startOffset']=32"
argument_list|,
literal|"//lst[@name='good ness']/int[@name='endOffset']=41"
argument_list|,
literal|"//lst[@name='good ness']/arr[@name='suggestion']/lst[1]/str[@name='word']='goodness'"
argument_list|,
literal|"//lst[@name='pine apple good ness']/int[@name='numFound']=1"
argument_list|,
literal|"//lst[@name='pine apple good ness']/int[@name='startOffset']=21"
argument_list|,
literal|"//lst[@name='pine apple good ness']/int[@name='endOffset']=41"
argument_list|,
literal|"//lst[@name='pine apple good ness']/arr[@name='suggestion']/lst[1]/str[@name='word']='pineapplegoodness'"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
DECL|method|testCollate
specifier|public
name|void
name|testCollate
parameter_list|()
throws|throws
name|Exception
block|{
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(paintable pine apple godness)"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_BUILD
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:(printable line ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][2 ]/str[@name='collationQuery']='lowerfilt:(paintablepine ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][3 ]/str[@name='collationQuery']='lowerfilt:(printable pineapple goodness)'"
argument_list|,
literal|"//lst[@name='collation'][4 ]/str[@name='collationQuery']='lowerfilt:((paint able) line ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][5 ]/str[@name='collationQuery']='lowerfilt:(printable (pi ne) ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][6 ]/str[@name='collationQuery']='lowerfilt:((paint able) pineapple goodness)'"
argument_list|,
literal|"//lst[@name='collation'][7 ]/str[@name='collationQuery']='lowerfilt:((paint able) (pi ne) ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][8 ]/str[@name='collationQuery']='lowerfilt:(pintable line ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][9 ]/str[@name='collationQuery']='lowerfilt:(pintable pineapple goodness)'"
argument_list|,
literal|"//lst[@name='collation'][10]/str[@name='collationQuery']='lowerfilt:(pintable (pi ne) ample goodness)'"
argument_list|,
literal|"//lst[@name='collation'][10]/lst[@name='misspellingsAndCorrections']/str[@name='paintable']='pintable'"
argument_list|,
literal|"//lst[@name='collation'][10]/lst[@name='misspellingsAndCorrections']/str[@name='pine']='pi ne'"
argument_list|,
literal|"//lst[@name='collation'][10]/lst[@name='misspellingsAndCorrections']/str[@name='apple']='ample'"
argument_list|,
literal|"//lst[@name='collation'][10]/lst[@name='misspellingsAndCorrections']/str[@name='godness']='goodness'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(pine AND apple)"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:(line AND ample)'"
argument_list|,
literal|"//lst[@name='collation'][2 ]/str[@name='collationQuery']='lowerfilt:(pineapple)'"
argument_list|,
literal|"//lst[@name='collation'][3 ]/str[@name='collationQuery']='lowerfilt:((pi AND ne) AND ample)'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:pine AND NOT lowerfilt:apple"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:line AND NOT lowerfilt:ample'"
argument_list|,
literal|"//lst[@name='collation'][2 ]/str[@name='collationQuery']='lowerfilt:(pi AND ne) AND NOT lowerfilt:ample'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:pine NOT lowerfilt:apple"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:line NOT lowerfilt:ample'"
argument_list|,
literal|"//lst[@name='collation'][2 ]/str[@name='collationQuery']='lowerfilt:(pi AND ne) NOT lowerfilt:ample'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(+pine -apple)"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|".75"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"10"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:(+line -ample)'"
argument_list|,
literal|"//lst[@name='collation'][2 ]/str[@name='collationQuery']='lowerfilt:((+pi +ne) -ample)'"
argument_list|)
expr_stmt|;
name|assertQ
argument_list|(
name|req
argument_list|(
literal|"q"
argument_list|,
literal|"lowerfilt:(+printableinpuntableplantable)"
argument_list|,
literal|"qt"
argument_list|,
literal|"spellCheckWithWordbreak"
argument_list|,
literal|"indent"
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|COMPONENT_NAME
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_ACCURACY
argument_list|,
literal|"1"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_COLLATE_EXTENDED_RESULTS
argument_list|,
literal|"true"
argument_list|,
name|SpellCheckComponent
operator|.
name|SPELLCHECK_MAX_COLLATIONS
argument_list|,
literal|"1"
argument_list|)
argument_list|,
literal|"//lst[@name='collation'][1 ]/str[@name='collationQuery']='lowerfilt:((+printable +in +puntable +plantable))'"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
