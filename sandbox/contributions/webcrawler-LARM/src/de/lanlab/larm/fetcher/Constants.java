begin_unit
begin_comment
comment|/* ====================================================================  * The Apache Software License, Version 1.1  *  * Copyright (c) 2001 The Apache Software Foundation.  All rights  * reserved.  *  * Redistribution and use in source and binary forms, with or without  * modification, are permitted provided that the following conditions  * are met:  *  * 1. Redistributions of source code must retain the above copyright  *    notice, this list of conditions and the following disclaimer.  *  * 2. Redistributions in binary form must reproduce the above copyright  *    notice, this list of conditions and the following disclaimer in  *    the documentation and/or other materials provided with the  *    distribution.  *  * 3. The end-user documentation included with the redistribution,  *    if any, must include the following acknowledgment:  *       "This product includes software developed by the  *        Apache Software Foundation (http://www.apache.org/)."  *    Alternately, this acknowledgment may appear in the software itself,  *    if and wherever such third-party acknowledgments normally appear.  *  * 4. The names "Apache" and "Apache Software Foundation" and  *    "Apache Lucene" must not be used to endorse or promote products  *    derived from this software without prior written permission. For  *    written permission, please contact apache@apache.org.  *  * 5. Products derived from this software may not be called "Apache",  *    "Apache Lucene", nor may "Apache" appear in their name, without  *    prior written permission of the Apache Software Foundation.  *  * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED  * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES  * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR  * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF  * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,  * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT  * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF  * SUCH DAMAGE.  * ====================================================================  *  * This software consists of voluntary contributions made by many  * individuals on behalf of the Apache Software Foundation.  For more  * information on the Apache Software Foundation, please see  *<http://www.apache.org/>.  */
end_comment
begin_package
DECL|package|de.lanlab.larm.fetcher
package|package
name|de
operator|.
name|lanlab
operator|.
name|larm
operator|.
name|fetcher
package|;
end_package
begin_comment
comment|/**  * contains all global constants used in this package  * @version $Id$  *  */
end_comment
begin_class
DECL|class|Constants
specifier|public
class|class
name|Constants
block|{
comment|/**      * user agent string a fetcher task gives to the corresponding server      */
DECL|field|USER_AGENT
specifier|public
specifier|static
specifier|final
name|String
name|USER_AGENT
init|=
literal|"Mozilla/4.06 [en] (WinNT; I)"
decl_stmt|;
comment|/**      * Crawler Identification      */
DECL|field|CRAWLER_AGENT
specifier|public
specifier|static
specifier|final
name|String
name|CRAWLER_AGENT
init|=
literal|"Fetcher/0.95"
decl_stmt|;
comment|/**      * size of the temporary buffer to read web documents in      */
DECL|field|FETCHERTASK_READSIZE
specifier|public
specifier|final
specifier|static
name|int
name|FETCHERTASK_READSIZE
init|=
literal|4096
decl_stmt|;
comment|/**      * don't read more than... bytes      */
DECL|field|FETCHERTASK_MAXFILESIZE
specifier|public
specifier|final
specifier|static
name|int
name|FETCHERTASK_MAXFILESIZE
init|=
literal|2000000
decl_stmt|;
block|}
end_class
end_unit
