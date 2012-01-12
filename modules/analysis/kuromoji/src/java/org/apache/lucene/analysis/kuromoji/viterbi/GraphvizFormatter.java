begin_unit
begin_package
DECL|package|org.apache.lucene.analysis.kuromoji.viterbi
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|kuromoji
operator|.
name|viterbi
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
name|HashMap
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
name|kuromoji
operator|.
name|dict
operator|.
name|ConnectionCosts
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
name|kuromoji
operator|.
name|viterbi
operator|.
name|ViterbiNode
operator|.
name|Type
import|;
end_import
begin_class
DECL|class|GraphvizFormatter
specifier|public
class|class
name|GraphvizFormatter
block|{
DECL|field|BOS_LABEL
specifier|private
specifier|final
specifier|static
name|String
name|BOS_LABEL
init|=
literal|"BOS"
decl_stmt|;
DECL|field|EOS_LABEL
specifier|private
specifier|final
specifier|static
name|String
name|EOS_LABEL
init|=
literal|"EOS"
decl_stmt|;
DECL|field|FONT_NAME
specifier|private
specifier|final
specifier|static
name|String
name|FONT_NAME
init|=
literal|"Helvetica"
decl_stmt|;
DECL|field|costs
specifier|private
name|ConnectionCosts
name|costs
decl_stmt|;
DECL|field|nodeMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|ViterbiNode
argument_list|>
name|nodeMap
decl_stmt|;
DECL|field|bestPathMap
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|bestPathMap
decl_stmt|;
DECL|field|foundBOS
specifier|private
name|boolean
name|foundBOS
decl_stmt|;
DECL|method|GraphvizFormatter
specifier|public
name|GraphvizFormatter
parameter_list|(
name|ConnectionCosts
name|costs
parameter_list|)
block|{
name|this
operator|.
name|costs
operator|=
name|costs
expr_stmt|;
name|this
operator|.
name|nodeMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|ViterbiNode
argument_list|>
argument_list|()
expr_stmt|;
name|this
operator|.
name|bestPathMap
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|ViterbiNode
index|[]
index|[]
name|startsArray
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|endsArray
parameter_list|)
block|{
name|initBestPathMap
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatHeader
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatNodes
argument_list|(
name|startsArray
argument_list|,
name|endsArray
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatTrailer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|format
specifier|public
name|String
name|format
parameter_list|(
name|ViterbiNode
index|[]
index|[]
name|startsArray
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|endsArray
parameter_list|,
name|List
argument_list|<
name|ViterbiNode
argument_list|>
name|bestPath
parameter_list|)
block|{
comment|//		List<ViterbiNode> bestPathWithBOSAndEOS = new ArrayList<ViterbiNode>(bastPath);
name|initBestPathMap
argument_list|(
name|bestPath
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatHeader
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatNodes
argument_list|(
name|startsArray
argument_list|,
name|endsArray
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatTrailer
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|initBestPathMap
specifier|private
name|void
name|initBestPathMap
parameter_list|(
name|List
argument_list|<
name|ViterbiNode
argument_list|>
name|bestPath
parameter_list|)
block|{
name|this
operator|.
name|bestPathMap
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|bestPath
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|bestPath
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
block|{
name|ViterbiNode
name|from
init|=
name|bestPath
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|ViterbiNode
name|to
init|=
name|bestPath
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|fromId
init|=
name|getNodeId
argument_list|(
name|from
argument_list|)
decl_stmt|;
name|String
name|toId
init|=
name|getNodeId
argument_list|(
name|to
argument_list|)
decl_stmt|;
assert|assert
name|this
operator|.
name|bestPathMap
operator|.
name|containsKey
argument_list|(
name|fromId
argument_list|)
operator|==
literal|false
assert|;
assert|assert
name|this
operator|.
name|bestPathMap
operator|.
name|containsValue
argument_list|(
name|toId
argument_list|)
operator|==
literal|false
assert|;
name|this
operator|.
name|bestPathMap
operator|.
name|put
argument_list|(
name|fromId
argument_list|,
name|toId
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|formatNodes
specifier|private
name|String
name|formatNodes
parameter_list|(
name|ViterbiNode
index|[]
index|[]
name|startsArray
parameter_list|,
name|ViterbiNode
index|[]
index|[]
name|endsArray
parameter_list|)
block|{
name|this
operator|.
name|nodeMap
operator|.
name|clear
argument_list|()
expr_stmt|;
name|this
operator|.
name|foundBOS
operator|=
literal|false
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|endsArray
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|endsArray
index|[
name|i
index|]
operator|==
literal|null
operator|||
name|startsArray
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|endsArray
index|[
name|i
index|]
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|ViterbiNode
name|from
init|=
name|endsArray
index|[
name|i
index|]
index|[
name|j
index|]
decl_stmt|;
if|if
condition|(
name|from
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|sb
operator|.
name|append
argument_list|(
name|formatNodeIfNew
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|startsArray
index|[
name|i
index|]
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
name|ViterbiNode
name|to
init|=
name|startsArray
index|[
name|i
index|]
index|[
name|k
index|]
decl_stmt|;
if|if
condition|(
name|to
operator|==
literal|null
condition|)
block|{
break|break;
block|}
name|sb
operator|.
name|append
argument_list|(
name|formatNodeIfNew
argument_list|(
name|to
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatEdge
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatNodeIfNew
specifier|private
name|String
name|formatNodeIfNew
parameter_list|(
name|ViterbiNode
name|node
parameter_list|)
block|{
name|String
name|nodeId
init|=
name|getNodeId
argument_list|(
name|node
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|this
operator|.
name|nodeMap
operator|.
name|containsKey
argument_list|(
name|nodeId
argument_list|)
condition|)
block|{
name|this
operator|.
name|nodeMap
operator|.
name|put
argument_list|(
name|nodeId
argument_list|,
name|node
argument_list|)
expr_stmt|;
return|return
name|formatNode
argument_list|(
name|node
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|""
return|;
block|}
block|}
DECL|method|formatHeader
specifier|private
name|String
name|formatHeader
parameter_list|()
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"digraph viterbi {\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"graph [ fontsize=30 labelloc=\"t\" label=\"\" splines=true overlap=false rankdir = \"LR\" ];\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"# A2 paper size\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"size = \"34.4,16.5\";\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"# try to fill paper\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"ratio = fill;\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"edge [ fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" fontcolor=\"red\" color=\"#606060\" ]\n"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"node [ style=\"filled\" fillcolor=\"#e8e8f0\" shape=\"Mrecord\" fontname=\""
operator|+
name|FONT_NAME
operator|+
literal|"\" ]\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatTrailer
specifier|private
name|String
name|formatTrailer
parameter_list|()
block|{
return|return
literal|"}"
return|;
block|}
DECL|method|formatEdge
specifier|private
name|String
name|formatEdge
parameter_list|(
name|ViterbiNode
name|from
parameter_list|,
name|ViterbiNode
name|to
parameter_list|)
block|{
if|if
condition|(
name|this
operator|.
name|bestPathMap
operator|.
name|containsKey
argument_list|(
name|getNodeId
argument_list|(
name|from
argument_list|)
argument_list|)
operator|&&
name|this
operator|.
name|bestPathMap
operator|.
name|get
argument_list|(
name|getNodeId
argument_list|(
name|from
argument_list|)
argument_list|)
operator|.
name|equals
argument_list|(
name|getNodeId
argument_list|(
name|to
argument_list|)
argument_list|)
condition|)
block|{
return|return
name|formatEdge
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|"color=\"#40e050\" fontcolor=\"#40a050\" penwidth=3 fontsize=20 "
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|formatEdge
argument_list|(
name|from
argument_list|,
name|to
argument_list|,
literal|""
argument_list|)
return|;
block|}
block|}
DECL|method|formatEdge
specifier|private
name|String
name|formatEdge
parameter_list|(
name|ViterbiNode
name|from
parameter_list|,
name|ViterbiNode
name|to
parameter_list|,
name|String
name|attributes
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeId
argument_list|(
name|from
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" -> "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeId
argument_list|(
name|to
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" [ "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"label=\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getCost
argument_list|(
name|from
argument_list|,
name|to
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|attributes
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" ]"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatNode
specifier|private
name|String
name|formatNode
parameter_list|(
name|ViterbiNode
name|node
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeId
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" [ "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"label="
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|formatNodeLabel
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" ]"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|formatNodeLabel
specifier|private
name|String
name|formatNodeLabel
parameter_list|(
name|ViterbiNode
name|node
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<<table border=\"0\" cellborder=\"0\">"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<tr><td>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getNodeLabel
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<tr><td>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<font color=\"blue\">"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|node
operator|.
name|getWordCost
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</font>"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</td></tr>"
argument_list|)
expr_stmt|;
comment|//		sb.append("<tr><td>");
comment|//		sb.append(this.dictionary.get(node.getWordId()).getPosInfo());
comment|//		sb.append("</td></tr>");
name|sb
operator|.
name|append
argument_list|(
literal|"</table>>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getNodeId
specifier|private
name|String
name|getNodeId
parameter_list|(
name|ViterbiNode
name|node
parameter_list|)
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|node
operator|.
name|hashCode
argument_list|()
argument_list|)
return|;
block|}
DECL|method|getNodeLabel
specifier|private
name|String
name|getNodeLabel
parameter_list|(
name|ViterbiNode
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|KNOWN
operator|&&
name|node
operator|.
name|getWordId
argument_list|()
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|foundBOS
condition|)
block|{
return|return
name|EOS_LABEL
return|;
block|}
else|else
block|{
name|this
operator|.
name|foundBOS
operator|=
literal|true
expr_stmt|;
return|return
name|BOS_LABEL
return|;
block|}
block|}
else|else
block|{
return|return
name|node
operator|.
name|getSurfaceFormString
argument_list|()
return|;
block|}
block|}
DECL|method|getCost
specifier|private
name|int
name|getCost
parameter_list|(
name|ViterbiNode
name|from
parameter_list|,
name|ViterbiNode
name|to
parameter_list|)
block|{
return|return
name|this
operator|.
name|costs
operator|.
name|get
argument_list|(
name|from
operator|.
name|getLeftId
argument_list|()
argument_list|,
name|to
operator|.
name|getRightId
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class
end_unit
