begin_unit
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_package
DECL|package|de.lanlab.larm.util
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|util
package|;
end_package
begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import
begin_class
DECL|class|ObservableInputStream
specifier|public
class|class
name|ObservableInputStream
extends|extends
name|FilterInputStream
block|{
DECL|field|reporting
specifier|private
name|boolean
name|reporting
init|=
literal|true
decl_stmt|;
DECL|field|startTime
specifier|private
name|long
name|startTime
decl_stmt|;
DECL|field|totalRead
specifier|private
name|int
name|totalRead
init|=
literal|0
decl_stmt|;
DECL|field|step
specifier|private
name|int
name|step
init|=
literal|1
decl_stmt|;
DECL|field|nextStep
specifier|private
name|int
name|nextStep
init|=
literal|0
decl_stmt|;
DECL|field|observer
name|InputStreamObserver
name|observer
decl_stmt|;
DECL|method|ObservableInputStream
specifier|public
name|ObservableInputStream
parameter_list|(
name|InputStream
name|in
parameter_list|,
name|InputStreamObserver
name|iso
parameter_list|,
name|int
name|reportingStep
parameter_list|)
block|{
name|super
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|observer
operator|=
name|iso
expr_stmt|;
name|observer
operator|.
name|notifyOpened
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
name|nextStep
operator|=
name|step
operator|=
name|reportingStep
expr_stmt|;
block|}
DECL|method|close
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|super
operator|.
name|close
argument_list|()
expr_stmt|;
name|observer
operator|.
name|notifyClosed
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|)
expr_stmt|;
block|}
DECL|method|setReporting
specifier|public
name|void
name|setReporting
parameter_list|(
name|boolean
name|reporting
parameter_list|)
block|{
name|this
operator|.
name|reporting
operator|=
name|reporting
expr_stmt|;
block|}
DECL|method|isReporting
specifier|public
name|boolean
name|isReporting
parameter_list|()
block|{
return|return
name|reporting
return|;
block|}
DECL|method|setReportingStep
specifier|public
name|void
name|setReportingStep
parameter_list|(
name|int
name|step
parameter_list|)
block|{
name|this
operator|.
name|step
operator|=
name|step
expr_stmt|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|readByte
init|=
name|super
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|readByte
operator|>=
literal|0
condition|?
literal|1
else|:
literal|0
argument_list|)
expr_stmt|;
block|}
return|return
name|readByte
return|;
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nrRead
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|nrRead
argument_list|)
expr_stmt|;
block|}
return|return
name|nrRead
return|;
block|}
DECL|method|notifyObserver
specifier|private
name|void
name|notifyObserver
parameter_list|(
name|int
name|nrRead
parameter_list|)
block|{
if|if
condition|(
name|nrRead
operator|>
literal|0
condition|)
block|{
name|totalRead
operator|+=
name|nrRead
expr_stmt|;
if|if
condition|(
name|totalRead
operator|>
name|nextStep
condition|)
block|{
name|nextStep
operator|+=
name|step
expr_stmt|;
name|observer
operator|.
name|notifyRead
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|nrRead
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|observer
operator|.
name|notifyFinished
argument_list|(
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
argument_list|,
name|totalRead
argument_list|)
expr_stmt|;
block|}
block|}
DECL|method|read
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|offs
parameter_list|,
name|int
name|size
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|nrRead
init|=
name|super
operator|.
name|read
argument_list|(
name|b
argument_list|,
name|offs
argument_list|,
name|size
argument_list|)
decl_stmt|;
if|if
condition|(
name|reporting
condition|)
block|{
name|notifyObserver
argument_list|(
name|nrRead
argument_list|)
expr_stmt|;
block|}
return|return
name|nrRead
return|;
block|}
block|}
end_class
end_unit
