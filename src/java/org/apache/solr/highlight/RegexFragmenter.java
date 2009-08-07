begin_unit
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_package
DECL|package|org.apache.solr.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|solr
operator|.
name|highlight
package|;
end_package
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|regex
operator|.
name|Matcher
import|;
end_import
begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|OffsetAttribute
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
name|PositionIncrementAttribute
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
name|search
operator|.
name|highlight
operator|.
name|Fragmenter
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
name|search
operator|.
name|highlight
operator|.
name|NullFragmenter
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
name|params
operator|.
name|DefaultSolrParams
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
name|params
operator|.
name|HighlightParams
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
name|params
operator|.
name|SolrParams
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
begin_comment
comment|/**  * Fragmenter that tries to produce snippets that "look" like a regular   * expression.  *  *<code>solrconfig.xml</code> parameters:  *<ul>  *<li><code>hl.regex.pattern</code>: regular expression corresponding to "nice" fragments.</li>  *<li><code>hl.regex.slop</code>: how far the fragmenter can stray from the ideal fragment size.        A slop of 0.2 means that the fragmenter can go over or under by 20%.</li>  *<li><code>hl.regex.maxAnalyzedChars</code>: how many characters to apply the        regular expression to (independent from the global highlighter setting).</li>  *</ul>  *  * NOTE: the default for<code>maxAnalyzedChars</code> is much lower for this   * fragmenter.  After this limit is exhausted, fragments are produced in the  * same way as<code>GapFragmenter</code>  */
end_comment
begin_class
DECL|class|RegexFragmenter
specifier|public
class|class
name|RegexFragmenter
extends|extends
name|HighlightingPluginBase
implements|implements
name|SolrFragmenter
block|{
DECL|field|defaultPatternRaw
specifier|protected
name|String
name|defaultPatternRaw
decl_stmt|;
DECL|field|defaultPattern
specifier|protected
name|Pattern
name|defaultPattern
decl_stmt|;
DECL|method|init
specifier|public
name|void
name|init
parameter_list|(
name|NamedList
name|args
parameter_list|)
block|{
name|super
operator|.
name|init
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|defaultPatternRaw
operator|=
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
expr_stmt|;
if|if
condition|(
name|defaults
operator|!=
literal|null
condition|)
block|{
name|defaultPatternRaw
operator|=
name|defaults
operator|.
name|get
argument_list|(
name|HighlightParams
operator|.
name|PATTERN
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
argument_list|)
expr_stmt|;
block|}
name|defaultPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|defaultPatternRaw
argument_list|)
expr_stmt|;
block|}
DECL|method|getFragmenter
specifier|public
name|Fragmenter
name|getFragmenter
parameter_list|(
name|String
name|fieldName
parameter_list|,
name|SolrParams
name|params
parameter_list|)
block|{
name|numRequests
operator|++
expr_stmt|;
if|if
condition|(
name|defaults
operator|!=
literal|null
condition|)
block|{
name|params
operator|=
operator|new
name|DefaultSolrParams
argument_list|(
name|params
argument_list|,
name|defaults
argument_list|)
expr_stmt|;
block|}
name|int
name|fragsize
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|FRAGSIZE
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_FRAGMENT_SIZE
argument_list|)
decl_stmt|;
name|int
name|increment
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|INCREMENT
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_INCREMENT_GAP
argument_list|)
decl_stmt|;
name|float
name|slop
init|=
name|params
operator|.
name|getFieldFloat
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|SLOP
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_SLOP
argument_list|)
decl_stmt|;
name|int
name|maxchars
init|=
name|params
operator|.
name|getFieldInt
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|MAX_RE_CHARS
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_MAX_ANALYZED_CHARS
argument_list|)
decl_stmt|;
name|String
name|rawpat
init|=
name|params
operator|.
name|getFieldParam
argument_list|(
name|fieldName
argument_list|,
name|HighlightParams
operator|.
name|PATTERN
argument_list|,
name|LuceneRegexFragmenter
operator|.
name|DEFAULT_PATTERN_RAW
argument_list|)
decl_stmt|;
name|Pattern
name|p
init|=
name|rawpat
operator|==
name|defaultPatternRaw
condition|?
name|defaultPattern
else|:
name|Pattern
operator|.
name|compile
argument_list|(
name|rawpat
argument_list|)
decl_stmt|;
if|if
condition|(
name|fragsize
operator|<=
literal|0
condition|)
block|{
return|return
operator|new
name|NullFragmenter
argument_list|()
return|;
block|}
return|return
operator|new
name|LuceneRegexFragmenter
argument_list|(
name|fragsize
argument_list|,
name|increment
argument_list|,
name|slop
argument_list|,
name|maxchars
argument_list|,
name|p
argument_list|)
return|;
block|}
comment|///////////////////////////////////////////////////////////////////////
comment|//////////////////////// SolrInfoMBeans methods ///////////////////////
comment|///////////////////////////////////////////////////////////////////////
annotation|@
name|Override
DECL|method|getDescription
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"RegexFragmenter ("
operator|+
name|defaultPatternRaw
operator|+
literal|")"
return|;
block|}
annotation|@
name|Override
DECL|method|getVersion
specifier|public
name|String
name|getVersion
parameter_list|()
block|{
return|return
literal|"$Revision$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSourceId
specifier|public
name|String
name|getSourceId
parameter_list|()
block|{
return|return
literal|"$Id$"
return|;
block|}
annotation|@
name|Override
DECL|method|getSource
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
literal|"$URL$"
return|;
block|}
block|}
end_class
begin_comment
comment|/**  * Fragmenter that tries to produce snippets that "look" like a regular   * expression.  *  * NOTE: the default for<code>maxAnalyzedChars</code> is much lower for this   * fragmenter.  After this limit is exhausted, fragments are produced in the  * same way as<code>GapFragmenter</code>  */
end_comment
begin_class
DECL|class|LuceneRegexFragmenter
class|class
name|LuceneRegexFragmenter
implements|implements
name|Fragmenter
block|{
comment|// ** defaults
DECL|field|DEFAULT_FRAGMENT_SIZE
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_FRAGMENT_SIZE
init|=
literal|70
decl_stmt|;
DECL|field|DEFAULT_INCREMENT_GAP
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_INCREMENT_GAP
init|=
literal|50
decl_stmt|;
DECL|field|DEFAULT_SLOP
specifier|public
specifier|static
specifier|final
name|float
name|DEFAULT_SLOP
init|=
literal|0.6f
decl_stmt|;
DECL|field|DEFAULT_MAX_ANALYZED_CHARS
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_MAX_ANALYZED_CHARS
init|=
literal|10000
decl_stmt|;
comment|// ** settings
comment|// desired length of fragments, in characters
DECL|field|targetFragChars
specifier|protected
name|int
name|targetFragChars
decl_stmt|;
comment|// increment gap which indicates a new fragment should occur
comment|// (often due to multi-valued fields)
DECL|field|incrementGapThreshold
specifier|protected
name|int
name|incrementGapThreshold
decl_stmt|;
comment|// factor by which we are allowed to bend the frag size (larger or smaller)
DECL|field|slop
specifier|protected
name|float
name|slop
decl_stmt|;
comment|// analysis limit (ensures we don't waste too much time on long fields)
DECL|field|maxAnalyzedChars
specifier|protected
name|int
name|maxAnalyzedChars
decl_stmt|;
comment|// default desirable pattern for text fragments.
DECL|field|textRE
specifier|protected
name|Pattern
name|textRE
decl_stmt|;
comment|// ** state
DECL|field|currentNumFrags
specifier|protected
name|int
name|currentNumFrags
decl_stmt|;
DECL|field|currentOffset
specifier|protected
name|int
name|currentOffset
decl_stmt|;
DECL|field|targetOffset
specifier|protected
name|int
name|targetOffset
decl_stmt|;
DECL|field|hotspots
specifier|protected
name|int
index|[]
name|hotspots
decl_stmt|;
DECL|field|posIncAtt
specifier|private
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
comment|// ** other
comment|// note: could dynamically change size of sentences extracted to match
comment|// target frag size
specifier|public
specifier|static
specifier|final
name|String
DECL|field|DEFAULT_PATTERN_RAW
name|DEFAULT_PATTERN_RAW
init|=
literal|"[-\\w ,\\n\"']{20,200}"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Pattern
DECL|field|DEFAULT_PATTERN
name|DEFAULT_PATTERN
init|=
name|Pattern
operator|.
name|compile
argument_list|(
name|DEFAULT_PATTERN_RAW
argument_list|)
decl_stmt|;
DECL|method|LuceneRegexFragmenter
specifier|public
name|LuceneRegexFragmenter
parameter_list|()
block|{
name|this
argument_list|(
name|DEFAULT_FRAGMENT_SIZE
argument_list|,
name|DEFAULT_INCREMENT_GAP
argument_list|,
name|DEFAULT_SLOP
argument_list|,
name|DEFAULT_MAX_ANALYZED_CHARS
argument_list|)
expr_stmt|;
block|}
DECL|method|LuceneRegexFragmenter
specifier|public
name|LuceneRegexFragmenter
parameter_list|(
name|int
name|targetFragChars
parameter_list|)
block|{
name|this
argument_list|(
name|targetFragChars
argument_list|,
name|DEFAULT_INCREMENT_GAP
argument_list|,
name|DEFAULT_SLOP
argument_list|,
name|DEFAULT_MAX_ANALYZED_CHARS
argument_list|)
expr_stmt|;
block|}
DECL|method|LuceneRegexFragmenter
specifier|public
name|LuceneRegexFragmenter
parameter_list|(
name|int
name|targetFragChars
parameter_list|,
name|int
name|incrementGapThreshold
parameter_list|,
name|float
name|slop
parameter_list|,
name|int
name|maxAnalyzedChars
parameter_list|)
block|{
name|this
argument_list|(
name|targetFragChars
argument_list|,
name|incrementGapThreshold
argument_list|,
name|slop
argument_list|,
name|maxAnalyzedChars
argument_list|,
name|DEFAULT_PATTERN
argument_list|)
expr_stmt|;
block|}
DECL|method|LuceneRegexFragmenter
specifier|public
name|LuceneRegexFragmenter
parameter_list|(
name|int
name|targetFragChars
parameter_list|,
name|int
name|incrementGapThreshold
parameter_list|,
name|float
name|slop
parameter_list|,
name|int
name|maxAnalyzedChars
parameter_list|,
name|Pattern
name|targetPattern
parameter_list|)
block|{
name|this
operator|.
name|targetFragChars
operator|=
name|targetFragChars
expr_stmt|;
name|this
operator|.
name|incrementGapThreshold
operator|=
name|incrementGapThreshold
expr_stmt|;
name|this
operator|.
name|slop
operator|=
name|slop
expr_stmt|;
name|this
operator|.
name|maxAnalyzedChars
operator|=
name|maxAnalyzedChars
expr_stmt|;
name|this
operator|.
name|textRE
operator|=
name|targetPattern
expr_stmt|;
block|}
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.TextFragmenter#start(java.lang.String)    */
DECL|method|start
specifier|public
name|void
name|start
parameter_list|(
name|String
name|originalText
parameter_list|,
name|TokenStream
name|tokenStream
parameter_list|)
block|{
name|currentNumFrags
operator|=
literal|1
expr_stmt|;
name|currentOffset
operator|=
literal|0
expr_stmt|;
name|addHotSpots
argument_list|(
name|originalText
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
operator|(
name|PositionIncrementAttribute
operator|)
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|offsetAtt
operator|=
operator|(
name|OffsetAttribute
operator|)
name|tokenStream
operator|.
name|getAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////
comment|// pre-analysis
comment|////////////////////////////////////
DECL|method|addHotSpots
specifier|protected
name|void
name|addHotSpots
parameter_list|(
name|String
name|text
parameter_list|)
block|{
comment|//System.out.println("hot spotting");
name|ArrayList
argument_list|<
name|Integer
argument_list|>
name|temphs
init|=
operator|new
name|ArrayList
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|text
operator|.
name|length
argument_list|()
operator|/
name|targetFragChars
argument_list|)
decl_stmt|;
name|Matcher
name|match
init|=
name|textRE
operator|.
name|matcher
argument_list|(
name|text
argument_list|)
decl_stmt|;
name|int
name|cur
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|match
operator|.
name|find
argument_list|()
operator|&&
name|cur
operator|<
name|maxAnalyzedChars
condition|)
block|{
name|int
name|start
init|=
name|match
operator|.
name|start
argument_list|()
decl_stmt|,
name|end
init|=
name|match
operator|.
name|end
argument_list|()
decl_stmt|;
name|temphs
operator|.
name|add
argument_list|(
name|start
argument_list|)
expr_stmt|;
name|temphs
operator|.
name|add
argument_list|(
name|end
argument_list|)
expr_stmt|;
name|cur
operator|=
name|end
expr_stmt|;
comment|//System.out.println("Matched " + match.group());
block|}
name|hotspots
operator|=
operator|new
name|int
index|[
name|temphs
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|temphs
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|hotspots
index|[
name|i
index|]
operator|=
name|temphs
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
comment|// perhaps not necessary--I don't know if re matches are non-overlapping
name|Arrays
operator|.
name|sort
argument_list|(
name|hotspots
argument_list|)
expr_stmt|;
block|}
comment|////////////////////////////////////
comment|// fragmenting
comment|////////////////////////////////////
comment|/* (non-Javadoc)    * @see org.apache.lucene.search.highlight.TextFragmenter#isNewFragment(org.apache.lucene.analysis.Token)    */
DECL|method|isNewFragment
specifier|public
name|boolean
name|isNewFragment
parameter_list|()
block|{
name|boolean
name|isNewFrag
init|=
literal|false
decl_stmt|;
name|int
name|minFragLen
init|=
call|(
name|int
call|)
argument_list|(
operator|(
literal|1.0f
operator|-
name|slop
operator|)
operator|*
name|targetFragChars
argument_list|)
decl_stmt|;
name|int
name|endOffset
init|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
decl_stmt|;
comment|// ** determin isNewFrag
if|if
condition|(
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
operator|>
name|incrementGapThreshold
condition|)
block|{
comment|// large position gaps always imply new fragments
name|isNewFrag
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|endOffset
operator|-
name|currentOffset
operator|<
name|minFragLen
condition|)
block|{
comment|// we're not in our range of flexibility
name|isNewFrag
operator|=
literal|false
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|targetOffset
operator|>
literal|0
condition|)
block|{
comment|// we've already decided on a target
name|isNewFrag
operator|=
name|endOffset
operator|>
name|targetOffset
expr_stmt|;
block|}
else|else
block|{
comment|// we might be able to do something
name|int
name|minOffset
init|=
name|currentOffset
operator|+
name|minFragLen
decl_stmt|;
name|int
name|maxOffset
init|=
call|(
name|int
call|)
argument_list|(
name|currentOffset
operator|+
operator|(
literal|1.0f
operator|+
name|slop
operator|)
operator|*
name|targetFragChars
argument_list|)
decl_stmt|;
name|int
name|hotIndex
decl_stmt|;
comment|// look for a close hotspot
name|hotIndex
operator|=
name|Arrays
operator|.
name|binarySearch
argument_list|(
name|hotspots
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
if|if
condition|(
name|hotIndex
operator|<
literal|0
condition|)
name|hotIndex
operator|=
operator|-
name|hotIndex
expr_stmt|;
if|if
condition|(
name|hotIndex
operator|>=
name|hotspots
operator|.
name|length
condition|)
block|{
comment|// no more hotspots in this input stream
name|targetOffset
operator|=
name|currentOffset
operator|+
name|targetFragChars
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|hotspots
index|[
name|hotIndex
index|]
operator|>
name|maxOffset
condition|)
block|{
comment|// no hotspots within slop
name|targetOffset
operator|=
name|currentOffset
operator|+
name|targetFragChars
expr_stmt|;
block|}
else|else
block|{
comment|// try to find hotspot in slop
name|int
name|goal
init|=
name|hotspots
index|[
name|hotIndex
index|]
decl_stmt|;
while|while
condition|(
name|goal
operator|<
name|minOffset
operator|&&
name|hotIndex
operator|<
name|hotspots
operator|.
name|length
condition|)
block|{
name|hotIndex
operator|++
expr_stmt|;
name|goal
operator|=
name|hotspots
index|[
name|hotIndex
index|]
expr_stmt|;
block|}
name|targetOffset
operator|=
name|goal
operator|<=
name|maxOffset
condition|?
name|goal
else|:
name|currentOffset
operator|+
name|targetFragChars
expr_stmt|;
block|}
name|isNewFrag
operator|=
name|endOffset
operator|>
name|targetOffset
expr_stmt|;
block|}
comment|// ** operate on isNewFrag
if|if
condition|(
name|isNewFrag
condition|)
block|{
name|currentNumFrags
operator|++
expr_stmt|;
name|currentOffset
operator|=
name|endOffset
expr_stmt|;
name|targetOffset
operator|=
operator|-
literal|1
expr_stmt|;
block|}
return|return
name|isNewFrag
return|;
block|}
block|}
end_class
end_unit
