begin_unit
begin_comment
comment|/*                     Egothor Software License version 1.00                     Copyright (C) 1997-2004 Leo Galambos.                  Copyright (C) 2002-2004 "Egothor developers"                       on behalf of the Egothor Project.                              All rights reserved.     This  software  is  copyrighted  by  the "Egothor developers". If this    license applies to a single file or document, the "Egothor developers"    are the people or entities mentioned as copyright holders in that file    or  document.  If  this  license  applies  to the Egothor project as a    whole,  the  copyright holders are the people or entities mentioned in    the  file CREDITS. This file can be found in the same location as this    license in the distribution.     Redistribution  and  use  in  source and binary forms, with or without    modification, are permitted provided that the following conditions are    met:     1. Redistributions  of  source  code  must retain the above copyright        notice, the list of contributors, this list of conditions, and the        following disclaimer.     2. Redistributions  in binary form must reproduce the above copyright        notice, the list of contributors, this list of conditions, and the        disclaimer  that  follows  these  conditions  in the documentation        and/or other materials provided with the distribution.     3. The name "Egothor" must not be used to endorse or promote products        derived  from  this software without prior written permission. For        written permission, please contact Leo.G@seznam.cz     4. Products  derived  from this software may not be called "Egothor",        nor  may  "Egothor"  appear  in  their name, without prior written        permission from Leo.G@seznam.cz.     In addition, we request that you include in the end-user documentation    provided  with  the  redistribution  and/or  in the software itself an    acknowledgement equivalent to the following:    "This product includes software developed by the Egothor Project.     http://egothor.sf.net/"     THIS  SOFTWARE  IS  PROVIDED  ``AS  IS''  AND ANY EXPRESSED OR IMPLIED    WARRANTIES,  INCLUDING,  BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF    MERCHANTABILITY  AND  FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.    IN  NO  EVENT  SHALL THE EGOTHOR PROJECT OR ITS CONTRIBUTORS BE LIABLE    FOR   ANY   DIRECT,   INDIRECT,  INCIDENTAL,  SPECIAL,  EXEMPLARY,  OR    CONSEQUENTIAL  DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF    SUBSTITUTE  GOODS  OR  SERVICES;  LOSS  OF  USE,  DATA, OR PROFITS; OR    BUSINESS  INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,    WHETHER  IN  CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE    OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN    IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.     This  software  consists  of  voluntary  contributions  made  by  many    individuals  on  behalf  of  the  Egothor  Project  and was originally    created by Leo Galambos (Leo.G@seznam.cz).  */
end_comment
begin_package
DECL|package|org.egothor.stemmer
package|package
name|org
operator|.
name|egothor
operator|.
name|stemmer
package|;
end_package
begin_comment
comment|/**  * The Diff object generates a patch string.  *<p>  * A patch string is actually a command to a stemmer telling it how to reduce a  * word to its root. For example, to reduce the word teacher to its root teach  * the patch string Db would be generated. This command tells the stemmer to  * delete the last 2 characters from the word teacher to reach the stem (the  * patch commands are applied starting from the last character in order to save  */
end_comment
begin_class
DECL|class|Diff
specifier|public
class|class
name|Diff
block|{
DECL|field|sizex
name|int
name|sizex
init|=
literal|0
decl_stmt|;
DECL|field|sizey
name|int
name|sizey
init|=
literal|0
decl_stmt|;
DECL|field|net
name|int
name|net
index|[]
index|[]
decl_stmt|;
DECL|field|way
name|int
name|way
index|[]
index|[]
decl_stmt|;
DECL|field|INSERT
name|int
name|INSERT
decl_stmt|;
DECL|field|DELETE
name|int
name|DELETE
decl_stmt|;
DECL|field|REPLACE
name|int
name|REPLACE
decl_stmt|;
DECL|field|NOOP
name|int
name|NOOP
decl_stmt|;
comment|/**    * Constructor for the Diff object.    */
DECL|method|Diff
specifier|public
name|Diff
parameter_list|()
block|{
name|this
argument_list|(
literal|1
argument_list|,
literal|1
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
comment|/**    * Constructor for the Diff object    *     * @param ins Description of the Parameter    * @param del Description of the Parameter    * @param rep Description of the Parameter    * @param noop Description of the Parameter    */
DECL|method|Diff
specifier|public
name|Diff
parameter_list|(
name|int
name|ins
parameter_list|,
name|int
name|del
parameter_list|,
name|int
name|rep
parameter_list|,
name|int
name|noop
parameter_list|)
block|{
name|INSERT
operator|=
name|ins
expr_stmt|;
name|DELETE
operator|=
name|del
expr_stmt|;
name|REPLACE
operator|=
name|rep
expr_stmt|;
name|NOOP
operator|=
name|noop
expr_stmt|;
block|}
comment|/**    * Apply the given patch string<tt>diff</tt> to the given string<tt>    * dest</tt>.    *     * @param dest Destination string    * @param diff Patch string    */
DECL|method|apply
specifier|public
specifier|static
name|void
name|apply
parameter_list|(
name|StringBuilder
name|dest
parameter_list|,
name|CharSequence
name|diff
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|diff
operator|==
literal|null
condition|)
block|{
return|return;
block|}
name|int
name|pos
init|=
name|dest
operator|.
name|length
argument_list|()
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|pos
operator|<
literal|0
condition|)
block|{
return|return;
block|}
comment|// orig == ""
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|diff
operator|.
name|length
argument_list|()
operator|/
literal|2
condition|;
name|i
operator|++
control|)
block|{
name|char
name|cmd
init|=
name|diff
operator|.
name|charAt
argument_list|(
literal|2
operator|*
name|i
argument_list|)
decl_stmt|;
name|char
name|param
init|=
name|diff
operator|.
name|charAt
argument_list|(
literal|2
operator|*
name|i
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|par_num
init|=
operator|(
name|param
operator|-
literal|'a'
operator|+
literal|1
operator|)
decl_stmt|;
switch|switch
condition|(
name|cmd
condition|)
block|{
case|case
literal|'-'
case|:
name|pos
operator|=
name|pos
operator|-
name|par_num
operator|+
literal|1
expr_stmt|;
break|break;
case|case
literal|'R'
case|:
name|dest
operator|.
name|setCharAt
argument_list|(
name|pos
argument_list|,
name|param
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'D'
case|:
name|int
name|o
init|=
name|pos
decl_stmt|;
name|pos
operator|-=
name|par_num
operator|-
literal|1
expr_stmt|;
comment|/*              * delete par_num chars from index pos              */
comment|// String s = orig.toString();
comment|// s = s.substring( 0, pos ) + s.substring( o + 1 );
comment|// orig = new StringBuffer( s );
name|dest
operator|.
name|delete
argument_list|(
name|pos
argument_list|,
name|o
operator|+
literal|1
argument_list|)
expr_stmt|;
break|break;
case|case
literal|'I'
case|:
name|dest
operator|.
name|insert
argument_list|(
name|pos
operator|+=
literal|1
argument_list|,
name|param
argument_list|)
expr_stmt|;
break|break;
block|}
name|pos
operator|--
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|StringIndexOutOfBoundsException
name|x
parameter_list|)
block|{
comment|// x.printStackTrace();
block|}
catch|catch
parameter_list|(
name|ArrayIndexOutOfBoundsException
name|x
parameter_list|)
block|{
comment|// x.printStackTrace();
block|}
block|}
comment|/**    * Construct a patch string that transforms a to b.    *     * @param a String 1st string    * @param b String 2nd string    * @return String    */
DECL|method|exec
specifier|public
specifier|synchronized
name|String
name|exec
parameter_list|(
name|String
name|a
parameter_list|,
name|String
name|b
parameter_list|)
block|{
if|if
condition|(
name|a
operator|==
literal|null
operator|||
name|b
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|int
name|x
decl_stmt|;
name|int
name|y
decl_stmt|;
name|int
name|maxx
decl_stmt|;
name|int
name|maxy
decl_stmt|;
name|int
name|go
index|[]
init|=
operator|new
name|int
index|[
literal|4
index|]
decl_stmt|;
specifier|final
name|int
name|X
init|=
literal|1
decl_stmt|;
specifier|final
name|int
name|Y
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|R
init|=
literal|3
decl_stmt|;
specifier|final
name|int
name|D
init|=
literal|0
decl_stmt|;
comment|/*      * setup memory if needed => processing speed up      */
name|maxx
operator|=
name|a
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
name|maxy
operator|=
name|b
operator|.
name|length
argument_list|()
operator|+
literal|1
expr_stmt|;
if|if
condition|(
operator|(
name|maxx
operator|>=
name|sizex
operator|)
operator|||
operator|(
name|maxy
operator|>=
name|sizey
operator|)
condition|)
block|{
name|sizex
operator|=
name|maxx
operator|+
literal|8
expr_stmt|;
name|sizey
operator|=
name|maxy
operator|+
literal|8
expr_stmt|;
name|net
operator|=
operator|new
name|int
index|[
name|sizex
index|]
index|[
name|sizey
index|]
expr_stmt|;
name|way
operator|=
operator|new
name|int
index|[
name|sizex
index|]
index|[
name|sizey
index|]
expr_stmt|;
block|}
comment|/*      * clear the network      */
for|for
control|(
name|x
operator|=
literal|0
init|;
name|x
operator|<
name|maxx
condition|;
name|x
operator|++
control|)
block|{
for|for
control|(
name|y
operator|=
literal|0
init|;
name|y
operator|<
name|maxy
condition|;
name|y
operator|++
control|)
block|{
name|net
index|[
name|x
index|]
index|[
name|y
index|]
operator|=
literal|0
expr_stmt|;
block|}
block|}
comment|/*      * set known persistent values      */
for|for
control|(
name|x
operator|=
literal|1
init|;
name|x
operator|<
name|maxx
condition|;
name|x
operator|++
control|)
block|{
name|net
index|[
name|x
index|]
index|[
literal|0
index|]
operator|=
name|x
expr_stmt|;
name|way
index|[
name|x
index|]
index|[
literal|0
index|]
operator|=
name|X
expr_stmt|;
block|}
for|for
control|(
name|y
operator|=
literal|1
init|;
name|y
operator|<
name|maxy
condition|;
name|y
operator|++
control|)
block|{
name|net
index|[
literal|0
index|]
index|[
name|y
index|]
operator|=
name|y
expr_stmt|;
name|way
index|[
literal|0
index|]
index|[
name|y
index|]
operator|=
name|Y
expr_stmt|;
block|}
for|for
control|(
name|x
operator|=
literal|1
init|;
name|x
operator|<
name|maxx
condition|;
name|x
operator|++
control|)
block|{
for|for
control|(
name|y
operator|=
literal|1
init|;
name|y
operator|<
name|maxy
condition|;
name|y
operator|++
control|)
block|{
name|go
index|[
name|X
index|]
operator|=
name|net
index|[
name|x
operator|-
literal|1
index|]
index|[
name|y
index|]
operator|+
name|DELETE
expr_stmt|;
comment|// way on x costs 1 unit
name|go
index|[
name|Y
index|]
operator|=
name|net
index|[
name|x
index|]
index|[
name|y
operator|-
literal|1
index|]
operator|+
name|INSERT
expr_stmt|;
comment|// way on y costs 1 unit
name|go
index|[
name|R
index|]
operator|=
name|net
index|[
name|x
operator|-
literal|1
index|]
index|[
name|y
operator|-
literal|1
index|]
operator|+
name|REPLACE
expr_stmt|;
name|go
index|[
name|D
index|]
operator|=
name|net
index|[
name|x
operator|-
literal|1
index|]
index|[
name|y
operator|-
literal|1
index|]
operator|+
operator|(
operator|(
name|a
operator|.
name|charAt
argument_list|(
name|x
operator|-
literal|1
argument_list|)
operator|==
name|b
operator|.
name|charAt
argument_list|(
name|y
operator|-
literal|1
argument_list|)
operator|)
condition|?
name|NOOP
else|:
literal|100
operator|)
expr_stmt|;
comment|// diagonal costs 0, when no change
name|short
name|min
init|=
name|D
decl_stmt|;
if|if
condition|(
name|go
index|[
name|min
index|]
operator|>=
name|go
index|[
name|X
index|]
condition|)
block|{
name|min
operator|=
name|X
expr_stmt|;
block|}
if|if
condition|(
name|go
index|[
name|min
index|]
operator|>
name|go
index|[
name|Y
index|]
condition|)
block|{
name|min
operator|=
name|Y
expr_stmt|;
block|}
if|if
condition|(
name|go
index|[
name|min
index|]
operator|>
name|go
index|[
name|R
index|]
condition|)
block|{
name|min
operator|=
name|R
expr_stmt|;
block|}
name|way
index|[
name|x
index|]
index|[
name|y
index|]
operator|=
name|min
expr_stmt|;
name|net
index|[
name|x
index|]
index|[
name|y
index|]
operator|=
operator|(
name|short
operator|)
name|go
index|[
name|min
index|]
expr_stmt|;
block|}
block|}
comment|// read the patch string
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|char
name|base
init|=
literal|'a'
operator|-
literal|1
decl_stmt|;
name|char
name|deletes
init|=
name|base
decl_stmt|;
name|char
name|equals
init|=
name|base
decl_stmt|;
for|for
control|(
name|x
operator|=
name|maxx
operator|-
literal|1
operator|,
name|y
operator|=
name|maxy
operator|-
literal|1
init|;
name|x
operator|+
name|y
operator|!=
literal|0
condition|;
control|)
block|{
switch|switch
condition|(
name|way
index|[
name|x
index|]
index|[
name|y
index|]
condition|)
block|{
case|case
name|X
case|:
if|if
condition|(
name|equals
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"-"
operator|+
operator|(
name|equals
operator|)
argument_list|)
expr_stmt|;
name|equals
operator|=
name|base
expr_stmt|;
block|}
name|deletes
operator|++
expr_stmt|;
name|x
operator|--
expr_stmt|;
break|break;
comment|// delete
case|case
name|Y
case|:
if|if
condition|(
name|deletes
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"D"
operator|+
operator|(
name|deletes
operator|)
argument_list|)
expr_stmt|;
name|deletes
operator|=
name|base
expr_stmt|;
block|}
if|if
condition|(
name|equals
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"-"
operator|+
operator|(
name|equals
operator|)
argument_list|)
expr_stmt|;
name|equals
operator|=
name|base
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'I'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|b
operator|.
name|charAt
argument_list|(
operator|--
name|y
argument_list|)
argument_list|)
expr_stmt|;
break|break;
comment|// insert
case|case
name|R
case|:
if|if
condition|(
name|deletes
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"D"
operator|+
operator|(
name|deletes
operator|)
argument_list|)
expr_stmt|;
name|deletes
operator|=
name|base
expr_stmt|;
block|}
if|if
condition|(
name|equals
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"-"
operator|+
operator|(
name|equals
operator|)
argument_list|)
expr_stmt|;
name|equals
operator|=
name|base
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|'R'
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|b
operator|.
name|charAt
argument_list|(
operator|--
name|y
argument_list|)
argument_list|)
expr_stmt|;
name|x
operator|--
expr_stmt|;
break|break;
comment|// replace
case|case
name|D
case|:
if|if
condition|(
name|deletes
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"D"
operator|+
operator|(
name|deletes
operator|)
argument_list|)
expr_stmt|;
name|deletes
operator|=
name|base
expr_stmt|;
block|}
name|equals
operator|++
expr_stmt|;
name|x
operator|--
expr_stmt|;
name|y
operator|--
expr_stmt|;
break|break;
comment|// no change
block|}
block|}
if|if
condition|(
name|deletes
operator|!=
name|base
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"D"
operator|+
operator|(
name|deletes
operator|)
argument_list|)
expr_stmt|;
name|deletes
operator|=
name|base
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class
end_unit
