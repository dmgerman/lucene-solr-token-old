begin_unit
begin_package
DECL|package|org.apache.lucene.search.suggest.analyzing
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|suggest
operator|.
name|analyzing
package|;
end_package
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|Analyzer
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
name|TokenStream
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
name|tokenattributes
operator|.
name|TermToBytesRefAttribute
import|;
end_import
begin_comment
comment|// javadocs
end_comment
begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
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
name|util
operator|.
name|IntsRef
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
name|util
operator|.
name|automaton
operator|.
name|Automaton
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
name|util
operator|.
name|automaton
operator|.
name|BasicAutomata
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
name|util
operator|.
name|automaton
operator|.
name|BasicOperations
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
name|util
operator|.
name|automaton
operator|.
name|LevenshteinAutomata
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
name|util
operator|.
name|automaton
operator|.
name|SpecialOperations
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
name|util
operator|.
name|fst
operator|.
name|FST
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
name|util
operator|.
name|fst
operator|.
name|PairOutputs
operator|.
name|Pair
import|;
end_import
begin_comment
comment|/**  * Implements a fuzzy {@link AnalyzingSuggester}. The similarity measurement is  * based on the Damerau-Levenshtein (optimal string alignment) algorithm, though  * you can explicitly choose classic Levenshtein by passing<code>false</code>  * for the<code>transpositions</code> parameter.  *<p>  * At most, this query will match terms up to  * {@value org.apache.lucene.util.automaton.LevenshteinAutomata#MAXIMUM_SUPPORTED_DISTANCE}  * edits. Higher distances are not supported.  Note that the  * fuzzy distance is measured in "byte space" on the bytes  * returned by the {@link TokenStream}'s {@link  * TermToBytesRefAttribute}, usually UTF8.  By default  * the analyzed bytes must be at least 3 {@link  * #DEFAULT_MIN_FUZZY_LENGTH} bytes before any edits are  * considered.  Furthermore, the first 1 {@link  * #DEFAULT_NON_FUZZY_PREFIX} byte is not allowed to be  * edited.  We allow up to 1 (@link  * #DEFAULT_MAX_EDITS} edit.  *  *<p>  * NOTE: This suggester does not boost suggestions that  * required no edits over suggestions that did require  * edits.  This is a known limitation.  *  *<p>  * Note: complex query analyzers can have a significant impact on the lookup  * performance. It's recommended to not use analyzers that drop or inject terms  * like synonyms to keep the complexity of the prefix intersection low for good  * lookup performance. At index time, complex analyzers can safely be used.  *</p>  */
end_comment
begin_class
DECL|class|FuzzySuggester
specifier|public
specifier|final
class|class
name|FuzzySuggester
extends|extends
name|AnalyzingSuggester
block|{
DECL|field|maxEdits
specifier|private
specifier|final
name|int
name|maxEdits
decl_stmt|;
DECL|field|transpositions
specifier|private
specifier|final
name|boolean
name|transpositions
decl_stmt|;
DECL|field|nonFuzzyPrefix
specifier|private
specifier|final
name|int
name|nonFuzzyPrefix
decl_stmt|;
DECL|field|minFuzzyLength
specifier|private
specifier|final
name|int
name|minFuzzyLength
decl_stmt|;
comment|/**    * The default minimum length of the key passed to {@link    * #lookup} before any edits are allowed.    */
DECL|field|DEFAULT_MIN_FUZZY_LENGTH
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MIN_FUZZY_LENGTH
init|=
literal|3
decl_stmt|;
comment|/**    * The default prefix length where edits are not allowed.    */
DECL|field|DEFAULT_NON_FUZZY_PREFIX
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_NON_FUZZY_PREFIX
init|=
literal|1
decl_stmt|;
comment|/**    * The default maximum number of edits for fuzzy    * suggestions.    */
DECL|field|DEFAULT_MAX_EDITS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_EDITS
init|=
literal|1
decl_stmt|;
comment|/**    * Creates a {@link FuzzySuggester} instance initialized with default values.    *     * @param analyzer the analyzer used for this suggester    */
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|analyzer
parameter_list|)
block|{
name|this
argument_list|(
name|analyzer
argument_list|,
name|analyzer
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link FuzzySuggester} instance with an index& a query analyzer initialized with default values.    *     * @param indexAnalyzer    *           Analyzer that will be used for analyzing suggestions while building the index.    * @param queryAnalyzer    *           Analyzer that will be used for analyzing query text during lookup    */
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|)
block|{
name|this
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|EXACT_FIRST
operator||
name|PRESERVE_SEP
argument_list|,
literal|256
argument_list|,
operator|-
literal|1
argument_list|,
name|DEFAULT_MAX_EDITS
argument_list|,
literal|true
argument_list|,
name|DEFAULT_NON_FUZZY_PREFIX
argument_list|,
name|DEFAULT_MIN_FUZZY_LENGTH
argument_list|)
expr_stmt|;
block|}
comment|/**    * Creates a {@link FuzzySuggester} instance.    *     * @param indexAnalyzer Analyzer that will be used for    *        analyzing suggestions while building the index.    * @param queryAnalyzer Analyzer that will be used for    *        analyzing query text during lookup    * @param options see {@link #EXACT_FIRST}, {@link #PRESERVE_SEP}    * @param maxSurfaceFormsPerAnalyzedForm Maximum number of    *        surface forms to keep for a single analyzed form.    *        When there are too many surface forms we discard the    *        lowest weighted ones.    * @param maxGraphExpansions Maximum number of graph paths    *        to expand from the analyzed form.  Set this to -1 for    *        no limit.    * @param maxEdits must be>= 0 and<= {@link LevenshteinAutomata#MAXIMUM_SUPPORTED_DISTANCE} .    * @param transpositions<code>true</code> if transpositions should be treated as a primitive     *        edit operation. If this is false, comparisons will implement the classic    *        Levenshtein algorithm.    * @param nonFuzzyPrefix length of common (non-fuzzy) prefix (see default {@link #DEFAULT_NON_FUZZY_PREFIX}    * @param minFuzzyLength minimum length of lookup key before any edits are allowed (see default {@link #DEFAULT_MIN_FUZZY_LENGTH})    */
DECL|method|FuzzySuggester
specifier|public
name|FuzzySuggester
parameter_list|(
name|Analyzer
name|indexAnalyzer
parameter_list|,
name|Analyzer
name|queryAnalyzer
parameter_list|,
name|int
name|options
parameter_list|,
name|int
name|maxSurfaceFormsPerAnalyzedForm
parameter_list|,
name|int
name|maxGraphExpansions
parameter_list|,
name|int
name|maxEdits
parameter_list|,
name|boolean
name|transpositions
parameter_list|,
name|int
name|nonFuzzyPrefix
parameter_list|,
name|int
name|minFuzzyLength
parameter_list|)
block|{
name|super
argument_list|(
name|indexAnalyzer
argument_list|,
name|queryAnalyzer
argument_list|,
name|options
argument_list|,
name|maxSurfaceFormsPerAnalyzedForm
argument_list|,
name|maxGraphExpansions
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxEdits
argument_list|<
literal|0
operator|||
name|maxEdits
argument_list|>
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"maxEdits must be between 0 and "
operator|+
name|LevenshteinAutomata
operator|.
name|MAXIMUM_SUPPORTED_DISTANCE
argument_list|)
throw|;
block|}
if|if
condition|(
name|nonFuzzyPrefix
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"nonFuzzyPrefix must not be>= 0 (got "
operator|+
name|nonFuzzyPrefix
operator|+
literal|")"
argument_list|)
throw|;
block|}
if|if
condition|(
name|minFuzzyLength
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minFuzzyLength must not be>= 0 (got "
operator|+
name|minFuzzyLength
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|this
operator|.
name|maxEdits
operator|=
name|maxEdits
expr_stmt|;
name|this
operator|.
name|transpositions
operator|=
name|transpositions
expr_stmt|;
name|this
operator|.
name|nonFuzzyPrefix
operator|=
name|nonFuzzyPrefix
expr_stmt|;
name|this
operator|.
name|minFuzzyLength
operator|=
name|minFuzzyLength
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getFullPrefixPaths
specifier|protected
name|List
argument_list|<
name|FSTUtil
operator|.
name|Path
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|getFullPrefixPaths
parameter_list|(
name|List
argument_list|<
name|FSTUtil
operator|.
name|Path
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
argument_list|>
name|prefixPaths
parameter_list|,
name|Automaton
name|lookupAutomaton
parameter_list|,
name|FST
argument_list|<
name|Pair
argument_list|<
name|Long
argument_list|,
name|BytesRef
argument_list|>
argument_list|>
name|fst
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO: right now there's no penalty for fuzzy/edits,
comment|// ie a completion whose prefix matched exactly what the
comment|// user typed gets no boost over completions that
comment|// required an edit, which get no boost over completions
comment|// requiring two edits.  I suspect a multiplicative
comment|// factor is appropriate (eg, say a fuzzy match must be at
comment|// least 2X better weight than the non-fuzzy match to
comment|// "compete") ... in which case I think the wFST needs
comment|// to be log weights or something ...
name|Automaton
name|levA
init|=
name|toLevenshteinAutomata
argument_list|(
name|lookupAutomaton
argument_list|)
decl_stmt|;
comment|/*       Writer w = new OutputStreamWriter(new FileOutputStream("out.dot"), "UTF-8");       w.write(levA.toDot());       w.close();       System.out.println("Wrote LevA to out.dot");     */
return|return
name|FSTUtil
operator|.
name|intersectPrefixPaths
argument_list|(
name|levA
argument_list|,
name|fst
argument_list|)
return|;
block|}
DECL|method|toLevenshteinAutomata
name|Automaton
name|toLevenshteinAutomata
parameter_list|(
name|Automaton
name|automaton
parameter_list|)
block|{
specifier|final
name|Set
argument_list|<
name|IntsRef
argument_list|>
name|ref
init|=
name|SpecialOperations
operator|.
name|getFiniteStrings
argument_list|(
name|automaton
argument_list|,
operator|-
literal|1
argument_list|)
decl_stmt|;
name|Automaton
name|subs
index|[]
init|=
operator|new
name|Automaton
index|[
name|ref
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|int
name|upto
init|=
literal|0
decl_stmt|;
for|for
control|(
name|IntsRef
name|path
range|:
name|ref
control|)
block|{
if|if
condition|(
name|path
operator|.
name|length
operator|<=
name|nonFuzzyPrefix
operator|||
name|path
operator|.
name|length
operator|<
name|minFuzzyLength
condition|)
block|{
name|subs
index|[
name|upto
index|]
operator|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
argument_list|,
name|path
operator|.
name|length
argument_list|)
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
else|else
block|{
name|Automaton
name|prefix
init|=
name|BasicAutomata
operator|.
name|makeString
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
argument_list|,
name|nonFuzzyPrefix
argument_list|)
decl_stmt|;
name|int
name|ints
index|[]
init|=
operator|new
name|int
index|[
name|path
operator|.
name|length
operator|-
name|nonFuzzyPrefix
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|path
operator|.
name|ints
argument_list|,
name|path
operator|.
name|offset
operator|+
name|nonFuzzyPrefix
argument_list|,
name|ints
argument_list|,
literal|0
argument_list|,
name|ints
operator|.
name|length
argument_list|)
expr_stmt|;
comment|// TODO: maybe add alphaMin to LevenshteinAutomata,
comment|// and pass 1 instead of 0?  We probably don't want
comment|// to allow the trailing dedup bytes to be
comment|// edited... but then 0 byte is "in general" allowed
comment|// on input (but not in UTF8).
name|LevenshteinAutomata
name|lev
init|=
operator|new
name|LevenshteinAutomata
argument_list|(
name|ints
argument_list|,
literal|255
argument_list|,
name|transpositions
argument_list|)
decl_stmt|;
name|Automaton
name|levAutomaton
init|=
name|lev
operator|.
name|toAutomaton
argument_list|(
name|maxEdits
argument_list|)
decl_stmt|;
name|Automaton
name|combined
init|=
name|BasicOperations
operator|.
name|concatenate
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|prefix
argument_list|,
name|levAutomaton
argument_list|)
argument_list|)
decl_stmt|;
name|combined
operator|.
name|setDeterministic
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// its like the special case in concatenate itself, except we cloneExpanded already
name|subs
index|[
name|upto
index|]
operator|=
name|combined
expr_stmt|;
name|upto
operator|++
expr_stmt|;
block|}
block|}
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|BasicAutomata
operator|.
name|makeEmpty
argument_list|()
return|;
comment|// matches nothing
block|}
elseif|else
if|if
condition|(
name|subs
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|subs
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
name|Automaton
name|a
init|=
name|BasicOperations
operator|.
name|union
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|subs
argument_list|)
argument_list|)
decl_stmt|;
comment|// TODO: we could call toLevenshteinAutomata() before det?
comment|// this only happens if you have multiple paths anyway (e.g. synonyms)
name|BasicOperations
operator|.
name|determinize
argument_list|(
name|a
argument_list|)
expr_stmt|;
comment|// Does not seem to help (and hurt maybe a bit: 6-9
comment|// prefix went from 19 to 18 kQPS):
comment|// a.reduce();
return|return
name|a
return|;
block|}
block|}
block|}
end_class
end_unit
