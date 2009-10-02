begin_unit
begin_package
DECL|package|org.apache.lucene.search.highlight
package|package
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|highlight
package|;
end_package
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
begin_comment
comment|/**  * Formats text with different color intensity depending on the score of the  * term using the span tag.  GradientFormatter uses a bgcolor argument to the font tag which  * doesn't work in Mozilla, thus this class.  *  * @see GradientFormatter  *  */
end_comment
begin_class
DECL|class|SpanGradientFormatter
specifier|public
class|class
name|SpanGradientFormatter
extends|extends
name|GradientFormatter
block|{
DECL|method|SpanGradientFormatter
specifier|public
name|SpanGradientFormatter
parameter_list|(
name|float
name|maxScore
parameter_list|,
name|String
name|minForegroundColor
parameter_list|,
name|String
name|maxForegroundColor
parameter_list|,
name|String
name|minBackgroundColor
parameter_list|,
name|String
name|maxBackgroundColor
parameter_list|)
block|{
name|super
argument_list|(
name|maxScore
argument_list|,
name|minForegroundColor
argument_list|,
name|maxForegroundColor
argument_list|,
name|minBackgroundColor
argument_list|,
name|maxBackgroundColor
argument_list|)
expr_stmt|;
block|}
DECL|method|highlightTerm
specifier|public
name|String
name|highlightTerm
parameter_list|(
name|String
name|originalText
parameter_list|,
name|TokenGroup
name|tokenGroup
parameter_list|)
block|{
if|if
condition|(
name|tokenGroup
operator|.
name|getTotalScore
argument_list|()
operator|==
literal|0
condition|)
return|return
name|originalText
return|;
name|float
name|score
init|=
name|tokenGroup
operator|.
name|getTotalScore
argument_list|()
decl_stmt|;
if|if
condition|(
name|score
operator|==
literal|0
condition|)
block|{
return|return
name|originalText
return|;
block|}
comment|// try to size sb correctly
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|originalText
operator|.
name|length
argument_list|()
operator|+
name|EXTRA
argument_list|)
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<span style=\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|highlightForeground
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"color: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getForegroundColorString
argument_list|(
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|highlightBackground
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"background: "
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|getBackgroundColorString
argument_list|(
name|score
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"; "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|originalText
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"</span>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
comment|// guess how much extra text we'll add to the text we're highlighting to try to avoid a  StringBuilder resize
DECL|field|TEMPLATE
specifier|private
specifier|static
specifier|final
name|String
name|TEMPLATE
init|=
literal|"<span style=\"background: #EEEEEE; color: #000000;\">...</span>"
decl_stmt|;
DECL|field|EXTRA
specifier|private
specifier|static
specifier|final
name|int
name|EXTRA
init|=
name|TEMPLATE
operator|.
name|length
argument_list|()
decl_stmt|;
block|}
end_class
end_unit
