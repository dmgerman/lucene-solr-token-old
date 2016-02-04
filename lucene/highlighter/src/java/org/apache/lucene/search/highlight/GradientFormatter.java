begin_unit
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment
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
comment|/**  * Formats text with different color intensity depending on the score of the  * term.  *  */
end_comment
begin_class
DECL|class|GradientFormatter
specifier|public
class|class
name|GradientFormatter
implements|implements
name|Formatter
block|{
DECL|field|maxScore
specifier|private
name|float
name|maxScore
decl_stmt|;
DECL|field|fgRMin
DECL|field|fgGMin
DECL|field|fgBMin
name|int
name|fgRMin
decl_stmt|,
name|fgGMin
decl_stmt|,
name|fgBMin
decl_stmt|;
DECL|field|fgRMax
DECL|field|fgGMax
DECL|field|fgBMax
name|int
name|fgRMax
decl_stmt|,
name|fgGMax
decl_stmt|,
name|fgBMax
decl_stmt|;
DECL|field|highlightForeground
specifier|protected
name|boolean
name|highlightForeground
decl_stmt|;
DECL|field|bgRMin
DECL|field|bgGMin
DECL|field|bgBMin
name|int
name|bgRMin
decl_stmt|,
name|bgGMin
decl_stmt|,
name|bgBMin
decl_stmt|;
DECL|field|bgRMax
DECL|field|bgGMax
DECL|field|bgBMax
name|int
name|bgRMax
decl_stmt|,
name|bgGMax
decl_stmt|,
name|bgBMax
decl_stmt|;
DECL|field|highlightBackground
specifier|protected
name|boolean
name|highlightBackground
decl_stmt|;
comment|/**      * Sets the color range for the IDF scores      *       * @param maxScore      *            The score (and above) displayed as maxColor (See QueryScorer.getMaxWeight       *         which can be used to calibrate scoring scale)      * @param minForegroundColor      *            The hex color used for representing IDF scores of zero eg      *            #FFFFFF (white) or null if no foreground color required      * @param maxForegroundColor      *            The largest hex color used for representing IDF scores eg      *            #000000 (black) or null if no foreground color required      * @param minBackgroundColor      *            The hex color used for representing IDF scores of zero eg      *            #FFFFFF (white) or null if no background color required      * @param maxBackgroundColor      *            The largest hex color used for representing IDF scores eg      *            #000000 (black) or null if no background color required      */
DECL|method|GradientFormatter
specifier|public
name|GradientFormatter
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
name|highlightForeground
operator|=
operator|(
name|minForegroundColor
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|maxForegroundColor
operator|!=
literal|null
operator|)
expr_stmt|;
if|if
condition|(
name|highlightForeground
condition|)
block|{
if|if
condition|(
name|minForegroundColor
operator|.
name|length
argument_list|()
operator|!=
literal|7
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minForegroundColor is not 7 bytes long eg a hex "
operator|+
literal|"RGB value such as #FFFFFF"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxForegroundColor
operator|.
name|length
argument_list|()
operator|!=
literal|7
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minForegroundColor is not 7 bytes long eg a hex "
operator|+
literal|"RGB value such as #FFFFFF"
argument_list|)
throw|;
block|}
name|fgRMin
operator|=
name|hexToInt
argument_list|(
name|minForegroundColor
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|fgGMin
operator|=
name|hexToInt
argument_list|(
name|minForegroundColor
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|fgBMin
operator|=
name|hexToInt
argument_list|(
name|minForegroundColor
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|fgRMax
operator|=
name|hexToInt
argument_list|(
name|maxForegroundColor
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|fgGMax
operator|=
name|hexToInt
argument_list|(
name|maxForegroundColor
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|fgBMax
operator|=
name|hexToInt
argument_list|(
name|maxForegroundColor
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|highlightBackground
operator|=
operator|(
name|minBackgroundColor
operator|!=
literal|null
operator|)
operator|&&
operator|(
name|maxBackgroundColor
operator|!=
literal|null
operator|)
expr_stmt|;
if|if
condition|(
name|highlightBackground
condition|)
block|{
if|if
condition|(
name|minBackgroundColor
operator|.
name|length
argument_list|()
operator|!=
literal|7
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minBackgroundColor is not 7 bytes long eg a hex "
operator|+
literal|"RGB value such as #FFFFFF"
argument_list|)
throw|;
block|}
if|if
condition|(
name|maxBackgroundColor
operator|.
name|length
argument_list|()
operator|!=
literal|7
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"minBackgroundColor is not 7 bytes long eg a hex "
operator|+
literal|"RGB value such as #FFFFFF"
argument_list|)
throw|;
block|}
name|bgRMin
operator|=
name|hexToInt
argument_list|(
name|minBackgroundColor
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|bgGMin
operator|=
name|hexToInt
argument_list|(
name|minBackgroundColor
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|bgBMin
operator|=
name|hexToInt
argument_list|(
name|minBackgroundColor
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
name|bgRMax
operator|=
name|hexToInt
argument_list|(
name|maxBackgroundColor
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
literal|3
argument_list|)
argument_list|)
expr_stmt|;
name|bgGMax
operator|=
name|hexToInt
argument_list|(
name|maxBackgroundColor
operator|.
name|substring
argument_list|(
literal|3
argument_list|,
literal|5
argument_list|)
argument_list|)
expr_stmt|;
name|bgBMax
operator|=
name|hexToInt
argument_list|(
name|maxBackgroundColor
operator|.
name|substring
argument_list|(
literal|5
argument_list|,
literal|7
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|//        this.corpusReader = corpusReader;
name|this
operator|.
name|maxScore
operator|=
name|maxScore
expr_stmt|;
comment|//        totalNumDocs = corpusReader.numDocs();
block|}
annotation|@
name|Override
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
literal|"<font "
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
literal|"color=\""
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
literal|"\" "
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
literal|"bgcolor=\""
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
literal|"\" "
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|">"
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
literal|"</font>"
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getForegroundColorString
specifier|protected
name|String
name|getForegroundColorString
parameter_list|(
name|float
name|score
parameter_list|)
block|{
name|int
name|rVal
init|=
name|getColorVal
argument_list|(
name|fgRMin
argument_list|,
name|fgRMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
name|int
name|gVal
init|=
name|getColorVal
argument_list|(
name|fgGMin
argument_list|,
name|fgGMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
name|int
name|bVal
init|=
name|getColorVal
argument_list|(
name|fgBMin
argument_list|,
name|fgBMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
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
literal|"#"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|rVal
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|gVal
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|bVal
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getBackgroundColorString
specifier|protected
name|String
name|getBackgroundColorString
parameter_list|(
name|float
name|score
parameter_list|)
block|{
name|int
name|rVal
init|=
name|getColorVal
argument_list|(
name|bgRMin
argument_list|,
name|bgRMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
name|int
name|gVal
init|=
name|getColorVal
argument_list|(
name|bgGMin
argument_list|,
name|bgGMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
name|int
name|bVal
init|=
name|getColorVal
argument_list|(
name|bgBMin
argument_list|,
name|bgBMax
argument_list|,
name|score
argument_list|)
decl_stmt|;
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
literal|"#"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|rVal
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|gVal
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|intToHex
argument_list|(
name|bVal
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
DECL|method|getColorVal
specifier|private
name|int
name|getColorVal
parameter_list|(
name|int
name|colorMin
parameter_list|,
name|int
name|colorMax
parameter_list|,
name|float
name|score
parameter_list|)
block|{
if|if
condition|(
name|colorMin
operator|==
name|colorMax
condition|)
block|{
return|return
name|colorMin
return|;
block|}
name|float
name|scale
init|=
name|Math
operator|.
name|abs
argument_list|(
name|colorMin
operator|-
name|colorMax
argument_list|)
decl_stmt|;
name|float
name|relScorePercent
init|=
name|Math
operator|.
name|min
argument_list|(
name|maxScore
argument_list|,
name|score
argument_list|)
operator|/
name|maxScore
decl_stmt|;
name|float
name|colScore
init|=
name|scale
operator|*
name|relScorePercent
decl_stmt|;
return|return
name|Math
operator|.
name|min
argument_list|(
name|colorMin
argument_list|,
name|colorMax
argument_list|)
operator|+
operator|(
name|int
operator|)
name|colScore
return|;
block|}
DECL|field|hexDigits
specifier|private
specifier|static
name|char
name|hexDigits
index|[]
init|=
block|{
literal|'0'
block|,
literal|'1'
block|,
literal|'2'
block|,
literal|'3'
block|,
literal|'4'
block|,
literal|'5'
block|,
literal|'6'
block|,
literal|'7'
block|,
literal|'8'
block|,
literal|'9'
block|,
literal|'A'
block|,
literal|'B'
block|,
literal|'C'
block|,
literal|'D'
block|,
literal|'E'
block|,
literal|'F'
block|}
decl_stmt|;
DECL|method|intToHex
specifier|private
specifier|static
name|String
name|intToHex
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
literal|""
operator|+
name|hexDigits
index|[
operator|(
name|i
operator|&
literal|0xF0
operator|)
operator|>>
literal|4
index|]
operator|+
name|hexDigits
index|[
name|i
operator|&
literal|0x0F
index|]
return|;
block|}
comment|/**      * Converts a hex string into an int. Integer.parseInt(hex, 16) assumes the      * input is nonnegative unless there is a preceding minus sign. This method      * reads the input as twos complement instead, so if the input is 8 bytes      * long, it will correctly restore a negative int produced by      * Integer.toHexString() but not necessarily one produced by      * Integer.toString(x,16) since that method will produce a string like '-FF'      * for negative integer values.      *       * @param hex      *            A string in capital or lower case hex, of no more then 16      *            characters.      * @throws NumberFormatException      *             if the string is more than 16 characters long, or if any      *             character is not in the set [0-9a-fA-f]      */
DECL|method|hexToInt
specifier|public
specifier|static
specifier|final
name|int
name|hexToInt
parameter_list|(
name|String
name|hex
parameter_list|)
block|{
name|int
name|len
init|=
name|hex
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|len
operator|>
literal|16
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|()
throw|;
name|int
name|l
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|len
condition|;
name|i
operator|++
control|)
block|{
name|l
operator|<<=
literal|4
expr_stmt|;
name|int
name|c
init|=
name|Character
operator|.
name|digit
argument_list|(
name|hex
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
argument_list|,
literal|16
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|<
literal|0
condition|)
throw|throw
operator|new
name|NumberFormatException
argument_list|()
throw|;
name|l
operator||=
name|c
expr_stmt|;
block|}
return|return
name|l
return|;
block|}
block|}
end_class
end_unit
