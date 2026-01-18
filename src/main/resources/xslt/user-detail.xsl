<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="userId"/>

    <xsl:template match="/">
        <html>
            <body>
                <xsl:for-each select="users/user[@id=$userId]">
                    <h1><xsl:value-of select="name"/></h1>
                    <p><b>Email:</b> <xsl:value-of select="email"/></p>
                    <p><b>Rôle:</b> <xsl:value-of select="@role"/></p>

                    <h3>Cours inscrits</h3>
                    <ul>
                        <xsl:for-each select="courses/courseRef">
                            <li>Course ID: <xsl:value-of select="@id"/></li>
                        </xsl:for-each>
                    </ul>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
