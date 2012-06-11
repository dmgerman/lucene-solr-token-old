begin_unit
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
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|CharTermAttribute
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
name|analysis
operator|.
name|tokenattributes
operator|.
name|PositionLengthAttribute
import|;
end_import
begin_comment
comment|/** Consumes a TokenStream and outputs the dot (graphviz) string (graph). */
end_comment
begin_class
DECL|class|TokenStreamToDot
specifier|public
class|class
name|TokenStreamToDot
block|{
DECL|field|in
specifier|private
specifier|final
name|TokenStream
name|in
decl_stmt|;
DECL|field|termAtt
specifier|private
specifier|final
name|CharTermAttribute
name|termAtt
decl_stmt|;
DECL|field|posIncAtt
specifier|private
specifier|final
name|PositionIncrementAttribute
name|posIncAtt
decl_stmt|;
DECL|field|posLengthAtt
specifier|private
specifier|final
name|PositionLengthAttribute
name|posLengthAtt
decl_stmt|;
DECL|field|offsetAtt
specifier|private
specifier|final
name|OffsetAttribute
name|offsetAtt
decl_stmt|;
DECL|field|inputText
specifier|private
specifier|final
name|String
name|inputText
decl_stmt|;
DECL|field|out
specifier|protected
specifier|final
name|PrintWriter
name|out
decl_stmt|;
comment|/** If inputText is non-null, and the TokenStream has    *  offsets, we include the surface form in each arc's    *  label. */
DECL|method|TokenStreamToDot
specifier|public
name|TokenStreamToDot
parameter_list|(
name|String
name|inputText
parameter_list|,
name|TokenStream
name|in
parameter_list|,
name|PrintWriter
name|out
parameter_list|)
block|{
name|this
operator|.
name|in
operator|=
name|in
expr_stmt|;
name|this
operator|.
name|out
operator|=
name|out
expr_stmt|;
name|this
operator|.
name|inputText
operator|=
name|inputText
expr_stmt|;
name|termAtt
operator|=
name|in
operator|.
name|addAttribute
argument_list|(
name|CharTermAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posIncAtt
operator|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionIncrementAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
name|posLengthAtt
operator|=
name|in
operator|.
name|addAttribute
argument_list|(
name|PositionLengthAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
if|if
condition|(
name|in
operator|.
name|hasAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
condition|)
block|{
name|offsetAtt
operator|=
name|in
operator|.
name|addAttribute
argument_list|(
name|OffsetAttribute
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|offsetAtt
operator|=
literal|null
expr_stmt|;
block|}
block|}
DECL|method|toDot
specifier|public
name|void
name|toDot
parameter_list|()
throws|throws
name|IOException
block|{
name|in
operator|.
name|reset
argument_list|()
expr_stmt|;
name|writeHeader
argument_list|()
expr_stmt|;
comment|// TODO: is there some way to tell dot that it should
comment|// make the "main path" a straight line and have the
comment|// non-sausage arcs not affect node placement...
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|lastEndPos
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|in
operator|.
name|incrementToken
argument_list|()
condition|)
block|{
specifier|final
name|boolean
name|isFirst
init|=
name|pos
operator|==
operator|-
literal|1
decl_stmt|;
name|int
name|posInc
init|=
name|posIncAtt
operator|.
name|getPositionIncrement
argument_list|()
decl_stmt|;
if|if
condition|(
name|isFirst
operator|&&
name|posInc
operator|==
literal|0
condition|)
block|{
comment|// TODO: hmm are TS's still allowed to do this...?
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARNING: first posInc was 0; correcting to 1"
argument_list|)
expr_stmt|;
name|posInc
operator|=
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|posInc
operator|>
literal|0
condition|)
block|{
comment|// New node:
name|pos
operator|+=
name|posInc
expr_stmt|;
name|writeNode
argument_list|(
name|pos
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|posInc
operator|>
literal|1
condition|)
block|{
comment|// Gap!
name|writeArc
argument_list|(
name|lastEndPos
argument_list|,
name|pos
argument_list|,
literal|null
argument_list|,
literal|"dotted"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isFirst
condition|)
block|{
name|writeNode
argument_list|(
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writeArc
argument_list|(
operator|-
literal|1
argument_list|,
name|pos
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|String
name|arcLabel
init|=
name|termAtt
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|offsetAtt
operator|!=
literal|null
condition|)
block|{
specifier|final
name|int
name|startOffset
init|=
name|offsetAtt
operator|.
name|startOffset
argument_list|()
decl_stmt|;
specifier|final
name|int
name|endOffset
init|=
name|offsetAtt
operator|.
name|endOffset
argument_list|()
decl_stmt|;
comment|//System.out.println("start=" + startOffset + " end=" + endOffset + " len=" + inputText.length());
if|if
condition|(
name|inputText
operator|!=
literal|null
condition|)
block|{
name|arcLabel
operator|+=
literal|" / "
operator|+
name|inputText
operator|.
name|substring
argument_list|(
name|startOffset
argument_list|,
name|endOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|arcLabel
operator|+=
literal|" / "
operator|+
name|startOffset
operator|+
literal|"-"
operator|+
name|endOffset
expr_stmt|;
block|}
block|}
name|writeArc
argument_list|(
name|pos
argument_list|,
name|pos
operator|+
name|posLengthAtt
operator|.
name|getPositionLength
argument_list|()
argument_list|,
name|arcLabel
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|lastEndPos
operator|=
name|pos
operator|+
name|posLengthAtt
operator|.
name|getPositionLength
argument_list|()
expr_stmt|;
block|}
name|in
operator|.
name|end
argument_list|()
expr_stmt|;
if|if
condition|(
name|lastEndPos
operator|!=
operator|-
literal|1
condition|)
block|{
comment|// TODO: should we output any final text (from end
comment|// offsets) on this arc...?
name|writeNode
argument_list|(
operator|-
literal|2
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|writeArc
argument_list|(
name|lastEndPos
argument_list|,
operator|-
literal|2
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|writeTrailer
argument_list|()
expr_stmt|;
block|}
DECL|method|writeArc
specifier|protected
name|void
name|writeArc
parameter_list|(
name|int
name|fromNode
parameter_list|,
name|int
name|toNode
parameter_list|,
name|String
name|label
parameter_list|,
name|String
name|style
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"  "
operator|+
name|fromNode
operator|+
literal|" -> "
operator|+
name|toNode
operator|+
literal|" ["
argument_list|)
expr_stmt|;
if|if
condition|(
name|label
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" label=\""
operator|+
name|label
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|style
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" style=\""
operator|+
name|style
operator|+
literal|"\""
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
DECL|method|writeNode
specifier|protected
name|void
name|writeNode
parameter_list|(
name|int
name|name
parameter_list|,
name|String
name|label
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"  "
operator|+
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|label
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|" [label=\""
operator|+
name|label
operator|+
literal|"\"]"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|print
argument_list|(
literal|" [shape=point color=white]"
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
DECL|field|FONT_NAME
specifier|private
specifier|final
specifier|static
name|String
name|FONT_NAME
init|=
literal|"Helvetica"
decl_stmt|;
comment|/** Override to customize. */
DECL|method|writeHeader
specifier|protected
name|void
name|writeHeader
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
literal|"digraph tokens {"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"LR\" ];"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  // A2 paper size"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  size = \"34.4,16.5\";"
argument_list|)
expr_stmt|;
comment|//out.println("  // try to fill paper");
comment|//out.println("  ratio = fill;");
name|out
operator|.
name|println
argument_list|(
literal|"  edge [ fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" fontcolor=\"red\" color=\"#606060\" ]"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"  node [ style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" ]"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
comment|/** Override to customize. */
DECL|method|writeTrailer
specifier|protected
name|void
name|writeTrailer
parameter_list|()
block|{
name|out
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
block|}
end_class
end_unit
