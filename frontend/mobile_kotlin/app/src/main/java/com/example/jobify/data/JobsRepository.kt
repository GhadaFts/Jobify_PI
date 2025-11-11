package com.example.jobify.data

import com.example.jobify.model.Job
import com.example.jobify.model.Applicant

// A fake repository that returns a static list of jobs.
class JobsRepository {
    fun getJobs(): List<Job> {
        return listOf(
            Job(
                id = "1",
                title = "Senior Frontend Developer",
                company = "Tech Corp",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "New York",
                jobType = "Full-time",
                shortDescription = "We are looking for an experienced Frontend Developer to join our team...",
                experience = "5+ years",
                salaryRange = "$120,000 - $150,000",
                applicantsCount = 2,
                skills = listOf("React", "TypeScript", "Next.js", "CSS"),
                badge = "actively hiring",
                published = true,
                postedAt = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000, // 2 days ago
                requirements = listOf(
                    "Bachelor's degree in Computer Science or related field",
                    "5+ years of professional frontend development experience"
                ),
                applicants = listOf(
                    Applicant(
                        id = "app1",
                        name = "Mohamed Ali",
                        title = "Senior Full Stack Developer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    ),
                    Applicant(
                        id = "app2",
                        name = "Fatima Zahra",
                        title = "Frontend Developer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    )
                )
            ),
            Job(
                id = "2",
                title = "Product Manager",
                company = "Innovate Labs",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "San Francisco",
                jobType = "Full-time",
                shortDescription = "Lead product development and strategy for our growing platform...",
                experience = "3+ years",
                salaryRange = "$100,000 - $130,000",
                applicantsCount = 1,
                skills = listOf("Product Strategy", "Agile", "User Research"),
                badge = "new",
                published = true,
                postedAt = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
                requirements = listOf(
                    "Proven experience as a Product Manager or similar role",
                    "Experience in product lifecycle management"
                ),
                applicants = listOf(
                    Applicant(
                        id = "app3",
                        name = "Sarah Johnson",
                        title = "Product Strategy Lead",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    )
                )
            ),
            Job(
                id = "3",
                title = "Backend Engineer",
                company = "Data Solutions",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "Austin",
                jobType = "Contract",
                shortDescription = "Design and implement scalable backend services.",
                experience = "4+ years",
                salaryRange = "$110,000 - $140,000",
                applicantsCount = 2,
                skills = listOf("Node.js", "GraphQL", "PostgreSQL", "AWS"),
                badge = "hot job",
                published = true,
                postedAt = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                requirements = listOf("Strong proficiency in Node.js", "Experience with cloud services (AWS, GCP, or Azure)"),
                applicants = listOf(
                    Applicant(
                        id = "app4",
                        name = "Alex Kumar",
                        title = "Backend Engineer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    ),
                    Applicant(
                        id = "app5",
                        name = "Emma Wilson",
                        title = "Node.js Specialist",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000,
                        isNew = false,
                        isFavorite = true,
                        status = "interview"
                    )
                )
            ),
            Job(
                id = "4",
                title = "UX/UI Designer",
                company = "Creative Minds",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "Remote",
                jobType = "Freelance",
                shortDescription = "Create compelling and user-friendly interfaces.",
                experience = "2+ years",
                salaryRange = "$70,000 - $90,000",
                applicantsCount = 3,
                skills = listOf("Figma", "Sketch", "Adobe XD"),
                badge = "limited openings",
                published = true,
                postedAt = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                requirements = listOf("A strong portfolio of successful UX/UI projects", "Excellent visual design skills"),
                applicants = listOf(
                    Applicant(
                        id = "app6",
                        name = "Maria Garcia",
                        title = "UI Designer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    ),
                    Applicant(
                        id = "app7",
                        name = "James Chen",
                        title = "UX/UI Specialist",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 4 * 24 * 60 * 60 * 1000,
                        isNew = false,
                        isFavorite = true,
                        status = "interview"
                    ),
                    Applicant(
                        id = "app8",
                        name = "Olivia Brown",
                        title = "Interaction Designer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    )
                )
            ),
            Job(
                id = "5",
                title = "DevOps Engineer",
                company = "InfraWorks",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "Boston",
                jobType = "Full-time",
                shortDescription = "Automate and streamline our operations and processes.",
                experience = "3+ years",
                salaryRange = "$115,000 - $135,000",
                applicantsCount = 2,
                skills = listOf("Kubernetes", "Docker", "CI/CD", "Terraform"),
                badge = "urgent hiring",
                published = true,
                postedAt = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000,
                requirements = listOf("Experience with CI/CD tools", "Proficiency in scripting languages"),
                applicants = listOf(
                    Applicant(
                        id = "app9",
                        name = "David Lee",
                        title = "DevOps Engineer",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000,
                        isNew = true,
                        isFavorite = false,
                        status = "new"
                    ),
                    Applicant(
                        id = "app10",
                        name = "Lisa Martinez",
                        title = "Infrastructure Specialist",
                        profileImageUrl = null,
                        appliedDate = System.currentTimeMillis() - 5 * 24 * 60 * 60 * 1000,
                        isNew = false,
                        isFavorite = false,
                        status = "new"
                    )
                )
            ),
            Job(
                id = "6",
                title = "Data Scientist",
                company = "Analytics Inc.",
                companyLogoUrl = "https://via.placeholder.com/150",
                location = "Chicago",
                jobType = "Full-time",
                shortDescription = "Turn raw data into actionable business insights.",
                experience = "4+ years",
                salaryRange = "$130,000 - $160,000",
                applicantsCount = 0,
                skills = listOf("Python", "R", "SQL", "Machine Learning"),
                badge = "open",
                published = false,
                postedAt = System.currentTimeMillis(),
                requirements = listOf("Strong experience with statistical analysis", "Hands-on experience with machine learning frameworks"),
                applicants = emptyList()
            )
        )
    }
}
