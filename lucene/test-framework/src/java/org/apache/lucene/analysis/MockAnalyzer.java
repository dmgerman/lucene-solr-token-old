begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.lucene.analysis
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Random
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
name|LuceneTestCase
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
name|CharacterRunAutomaton
import|;
end_import
begin_comment
comment|/**  * Analyzer for testing  *<p>  * This analyzer is a replacement for Whitespace/Simple/KeywordAnalyzers  * for unit tests. If you are testing a custom component such as a queryparser  * or analyzer-wrapper that consumes analysis streams, it's a great idea to test  * it with this analyzer instead. MockAnalyzer has the following behavior:  *<ul>  *<li>By default, the assertions in {@link MockTokenizer} are turned on for extra  *       checks that the consumer is consuming properly. These checks can be disabled  *       with {@link #setEnableChecks(boolean)}.  *<li>Payload data is randomly injected into the stream for more thorough testing  *       of payloads.  *</ul>  * @see MockTokenizer  */
end_comment
begin_class
DECL|class|MockAnalyzer
specifier|public
specifier|final
class|class
name|MockAnalyzer
extends|extends
name|Analyzer
block|{
DECL|field|runAutomaton
specifier|private
specifier|final
name|CharacterRunAutomaton
name|runAutomaton
decl_stmt|;
DECL|field|lowerCase
specifier|private
specifier|final
name|boolean
name|lowerCase
decl_stmt|;
DECL|field|filter
specifier|private
specifier|final
name|CharacterRunAutomaton
name|filter
decl_stmt|;
DECL|field|positionIncrementGap
specifier|private
name|int
name|positionIncrementGap
decl_stmt|;
DECL|field|offsetGap
specifier|private
name|Integer
name|offsetGap
decl_stmt|;
DECL|field|random
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
DECL|field|previousMappings
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|previousMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
DECL|field|enableChecks
specifier|private
name|boolean
name|enableChecks
init|=
literal|true
decl_stmt|;
DECL|field|maxTokenLength
specifier|private
name|int
name|maxTokenLength
init|=
name|MockTokenizer
operator|.
name|DEFAULT_MAX_TOKEN_LENGTH
decl_stmt|;
comment|/**    * Creates a new MockAnalyzer.    *     * @param random Random for payloads behavior    * @param runAutomaton DFA describing how tokenization should happen (e.g. [a-zA-Z]+)    * @param lowerCase true if the tokenizer should lowercase terms    * @param filter DFA describing how terms should be filtered (set of stopwords, etc)    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|,
name|CharacterRunAutomaton
name|filter
parameter_list|)
block|{
name|super
argument_list|(
name|PER_FIELD_REUSE_STRATEGY
argument_list|)
expr_stmt|;
comment|// TODO: this should be solved in a different way; Random should not be shared (!).
name|this
operator|.
name|random
operator|=
operator|new
name|Random
argument_list|(
name|random
operator|.
name|nextLong
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|runAutomaton
operator|=
name|runAutomaton
expr_stmt|;
name|this
operator|.
name|lowerCase
operator|=
name|lowerCase
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
block|}
comment|/**    * Calls {@link #MockAnalyzer(Random, CharacterRunAutomaton, boolean, CharacterRunAutomaton)     * MockAnalyzer(random, runAutomaton, lowerCase, MockTokenFilter.EMPTY_STOPSET, false}).    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|,
name|CharacterRunAutomaton
name|runAutomaton
parameter_list|,
name|boolean
name|lowerCase
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|MockTokenFilter
operator|.
name|EMPTY_STOPSET
argument_list|)
expr_stmt|;
block|}
comment|/**     * Create a Whitespace-lowercasing analyzer with no stopwords removal.    *<p>    * Calls {@link #MockAnalyzer(Random, CharacterRunAutomaton, boolean, CharacterRunAutomaton)     * MockAnalyzer(random, MockTokenizer.WHITESPACE, true, MockTokenFilter.EMPTY_STOPSET, false}).    */
DECL|method|MockAnalyzer
specifier|public
name|MockAnalyzer
parameter_list|(
name|Random
name|random
parameter_list|)
block|{
name|this
argument_list|(
name|random
argument_list|,
name|MockTokenizer
operator|.
name|WHITESPACE
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|createComponents
specifier|public
name|TokenStreamComponents
name|createComponents
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
name|MockTokenizer
name|tokenizer
init|=
operator|new
name|MockTokenizer
argument_list|(
name|runAutomaton
argument_list|,
name|lowerCase
argument_list|,
name|maxTokenLength
argument_list|)
decl_stmt|;
name|tokenizer
operator|.
name|setEnableChecks
argument_list|(
name|enableChecks
argument_list|)
expr_stmt|;
name|MockTokenFilter
name|filt
init|=
operator|new
name|MockTokenFilter
argument_list|(
name|tokenizer
argument_list|,
name|filter
argument_list|)
decl_stmt|;
return|return
operator|new
name|TokenStreamComponents
argument_list|(
name|tokenizer
argument_list|,
name|maybePayload
argument_list|(
name|filt
argument_list|,
name|fieldName
argument_list|)
argument_list|)
return|;
block|}
DECL|method|maybePayload
specifier|private
specifier|synchronized
name|TokenFilter
name|maybePayload
parameter_list|(
name|TokenFilter
name|stream
parameter_list|,
name|String
name|fieldName
parameter_list|)
block|{
name|Integer
name|val
init|=
name|previousMappings
operator|.
name|get
argument_list|(
name|fieldName
argument_list|)
decl_stmt|;
if|if
condition|(
name|val
operator|==
literal|null
condition|)
block|{
name|val
operator|=
operator|-
literal|1
expr_stmt|;
comment|// no payloads
if|if
condition|(
name|LuceneTestCase
operator|.
name|rarely
argument_list|(
name|random
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|random
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
condition|)
block|{
case|case
literal|0
case|:
name|val
operator|=
operator|-
literal|1
expr_stmt|;
comment|// no payloads
break|break;
case|case
literal|1
case|:
name|val
operator|=
name|Integer
operator|.
name|MAX_VALUE
expr_stmt|;
comment|// variable length payload
break|break;
case|case
literal|2
case|:
name|val
operator|=
name|random
operator|.
name|nextInt
argument_list|(
literal|12
argument_list|)
expr_stmt|;
comment|// fixed length payload
break|break;
block|}
block|}
if|if
condition|(
name|LuceneTestCase
operator|.
name|VERBOSE
condition|)
block|{
if|if
condition|(
name|val
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockAnalyzer: field="
operator|+
name|fieldName
operator|+
literal|" gets variable length payloads"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|val
operator|!=
operator|-
literal|1
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MockAnalyzer: field="
operator|+
name|fieldName
operator|+
literal|" gets fixed length="
operator|+
name|val
operator|+
literal|" payloads"
argument_list|)
expr_stmt|;
block|}
block|}
name|previousMappings
operator|.
name|put
argument_list|(
name|fieldName
argument_list|,
name|val
argument_list|)
expr_stmt|;
comment|// save it so we are consistent for this field
block|}
if|if
condition|(
name|val
operator|==
operator|-
literal|1
condition|)
return|return
name|stream
return|;
elseif|else
if|if
condition|(
name|val
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|)
return|return
operator|new
name|MockVariableLengthPayloadFilter
argument_list|(
name|random
argument_list|,
name|stream
argument_list|)
return|;
else|else
return|return
operator|new
name|MockFixedLengthPayloadFilter
argument_list|(
name|random
argument_list|,
name|stream
argument_list|,
name|val
argument_list|)
return|;
block|}
DECL|method|setPositionIncrementGap
specifier|public
name|void
name|setPositionIncrementGap
parameter_list|(
name|int
name|positionIncrementGap
parameter_list|)
block|{
name|this
operator|.
name|positionIncrementGap
operator|=
name|positionIncrementGap
expr_stmt|;
block|}
annotation|@
name|Override
DECL|method|getPositionIncrementGap
specifier|public
name|int
name|getPositionIncrementGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|positionIncrementGap
return|;
block|}
comment|/**    * Set a new offset gap which will then be added to the offset when several fields with the same name are indexed    * @param offsetGap The offset gap that should be used.    */
DECL|method|setOffsetGap
specifier|public
name|void
name|setOffsetGap
parameter_list|(
name|int
name|offsetGap
parameter_list|)
block|{
name|this
operator|.
name|offsetGap
operator|=
name|offsetGap
expr_stmt|;
block|}
comment|/**    * Get the offset gap between tokens in fields if several fields with the same name were added.    * @param fieldName Currently not used, the same offset gap is returned for each field.    */
annotation|@
name|Override
DECL|method|getOffsetGap
specifier|public
name|int
name|getOffsetGap
parameter_list|(
name|String
name|fieldName
parameter_list|)
block|{
return|return
name|offsetGap
operator|==
literal|null
condition|?
name|super
operator|.
name|getOffsetGap
argument_list|(
name|fieldName
argument_list|)
else|:
name|offsetGap
return|;
block|}
comment|/**     * Toggle consumer workflow checking: if your test consumes tokenstreams normally you    * should leave this enabled.    */
DECL|method|setEnableChecks
specifier|public
name|void
name|setEnableChecks
parameter_list|(
name|boolean
name|enableChecks
parameter_list|)
block|{
name|this
operator|.
name|enableChecks
operator|=
name|enableChecks
expr_stmt|;
block|}
comment|/**     * Toggle maxTokenLength for MockTokenizer    */
DECL|method|setMaxTokenLength
specifier|public
name|void
name|setMaxTokenLength
parameter_list|(
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|maxTokenLength
operator|=
name|length
expr_stmt|;
block|}
block|}
end_class
end_unit
